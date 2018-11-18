package org.c4i.nlp.match;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.c4i.chitchat.api.model.LanguageProcessingConfig;
import org.c4i.nlp.Nlp;
import org.c4i.nlp.generalize.Generalizer;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.util.StringUtil;
import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ToStringFormatter;
import org.parboiled.support.Var;
import org.parboiled.trees.GraphNode;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.parboiled.trees.GraphUtils.printTree;

/**
 * Compiles a match pattern into a form, {@link Script}, that can be evaluated ({@link Eval}.
 *
 * @author Arvid Halma
 * @version 23-11-2015
 */
@SuppressWarnings("WeakerAccess")
@BuildParseTree
public class Compiler extends BaseParser<OperatorNode> {
    static final Pattern INDENT_PATTERN = Pattern.compile("^[ \t]*");

    // arrow symbol
    static final Pattern RULE_PATTERN = Pattern.compile("(?U)^[ \t]*@(\\w+) *([{](.*?)[}])? *<- *(.*)", Pattern.DOTALL);
    static final Pattern REPLY_PATTERN = Pattern.compile("^[ \t]*(.*?) *([{](.*?)[}])? *-> *(.*)", Pattern.DOTALL);
    static final Pattern PREFIX_BLOCK_PATTERN = Pattern.compile("^[ \t]*(.*?) *\\{ *$");

    static final ObjectMapper OBJECT_MAPPER;
    static {
        OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
        OBJECT_MAPPER.registerModule(new JodaModule());
    }

    /**
     * Compile source code to an evaluable/executable form.
     * @param src the source containing the rules
     * @return an internal representation of the rules
     */
    public static Script compile(String src) {
        return compile(src, new Nlp(null, ImmutableMap.of("en", new LanguageProcessingConfig())));
    }

    /**
     * Compile source code to an evaluable/executable form.
     * @param src the source containing the rules
     * @param nlp natural languange processing resource
     * @return an internal representation of the rules
     */
    public static Script compile(String src, Nlp nlp) {
        if(src == null){
            throw new ParseError("The rule set source cannot be NULL.");
        }

        // Pre-process ...

        // 1. Split config and rule sections
        String[] lines = src.split("\\R");
        StringBuilder configBuilder = new StringBuilder();
        StringBuilder rulesBuilder = new StringBuilder();
        String config, rules;
        int rulesLineOffset = 1;
        boolean appendToRules = false;
        int yamlMarkerCount = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if("---".equals(line)){
                yamlMarkerCount++;
                if(yamlMarkerCount >= 2) {
                    appendToRules = true;
                    rulesLineOffset = i + 1;
                    continue;
                }
            }
            if(appendToRules){
                rulesBuilder.append(line).append("\n");
            } else {
                configBuilder.append(line).append("\n");
            }
        }

        ScriptConfig scriptConfig = new ScriptConfig();
        if(!appendToRules) {
            // there was no config section...
            rules = configBuilder.toString();
        } else {
            // get yaml config
            config = configBuilder.toString();

            try{
                scriptConfig = OBJECT_MAPPER.readValue(config, ScriptConfig.class);
            } catch (IOException e){
                String message = e.getMessage();
                if(!message.startsWith("No content to map")){
                    // only when config section is non-empty
                    throw new ParseError(e);
                }
            }
            rules = rulesBuilder.toString();
        }

        // 2. remove comments
        rules = StringUtil.removeLineComments(rules, "#");

        // 3. normalize emoticons
        /*if(matchConfig.getLanguages().contains("emoticons")){
            rules = EmojiAlias.matchCodify(rules);
        }*/


        // 4. create (multi-line) statements from single lines and compile
        String[] ruleLines = rules.split("\n");


        StringNormalizer normalizer = nlp.getNormalizer(scriptConfig);
        Boolean optimizeRuleLogic = scriptConfig.getOptimizeRuleLogic();
        List<LabelRule> ruleList = new ArrayList<>();
        List<ReplyRule> replyList = new ArrayList<>();


        int lineNr = 1;
        int statementLineNr = 1;
        int tmpStatementLineNr = 1;
        try {
            String statement = "", tmpStatement = "", lastIndent = "";
            boolean allowContinueStatement = false;
            boolean closePrefixBlock = false;
            List<String> prefixes = new ArrayList<>();


            for (int i = 0; i <= ruleLines.length; i++) {
                lineNr = rulesLineOffset + i;
                boolean processStatement = false; // buffer input mode

                if(i == ruleLines.length){
                    // process last pending statement
                    statement = tmpStatement;
                    statementLineNr = tmpStatementLineNr;
                    processStatement = true;
                } else {
                    String line = ruleLines[i];
                    final String trim = line.trim();

                    // Prefix block cases
                    final Matcher prefixBlockMatcher = PREFIX_BLOCK_PATTERN.matcher(line);
                    if(prefixBlockMatcher.find()){
                        prefixes.add(prefixes.size(), prefixBlockMatcher.group(1));
                        allowContinueStatement = false;
                        continue;
                    }

                    if (trim.equals("}")) {
                        if(prefixes.isEmpty()) {
                            statementLineNr = 1;
                            throw new ParseError("No prefix block to be closed.", lineNr);
                        }
                        closePrefixBlock = true;
                        allowContinueStatement = false;
                    } else {
                        closePrefixBlock = false;
                    }


                    Matcher indentMatcher = INDENT_PATTERN.matcher(line);
                    String indent = "";
                    if(indentMatcher.find()) {
                        indent = indentMatcher.group();
                    }

                    if (allowContinueStatement && indent.length() > lastIndent.length()) {
                        // buffer input: line continuation
                        if(REPLY_PATTERN.matcher(line).find()){
                            statementLineNr = 1;
                            throw new ParseError("Improper indentation: this line defines a reply (... -> ...) and is not a continuation of the last one.", lineNr);
                        }
                        tmpStatement += line + "\n";
                    } else {
                        // start of new rule
                        allowContinueStatement = true;
                        statementLineNr = tmpStatementLineNr;
                        tmpStatementLineNr = lineNr;
                        statement = tmpStatement;
                        if (!trim.isEmpty() & !closePrefixBlock) {
                            tmpStatement = line + "\n";
                        } else {
                            tmpStatement = "";
                            allowContinueStatement = false;
                        }
                        processStatement = i >= 1; // not first time, unless it is the only one
                        lastIndent = indent;
                    }
                }


                if(!processStatement){
                    continue; // keep collecting multi line inputs
                }

                if(statement.trim().isEmpty()){
                    continue; // ignore empty lines
                }


                Matcher replyMatcher = REPLY_PATTERN.matcher(statement);
                if (replyMatcher.find()) {
                    // reply rule

                    // match groups:
                    // reply\((.*?)\) *([{](.*?)[}])? *= *(.*)
                    // gr =     1      2   3              4

                    String head = String.join(" ", prefixes) + " " + replyMatcher.group(1).trim();
                    String optionsText = replyMatcher.group(2);
                    LinkedHashMap<String,Object> props = null;
                    if(optionsText != null){
                        try {
                            props = OBJECT_MAPPER.readValue(optionsText, new TypeReference<LinkedHashMap<String,Object>>() {});
                        } catch (IOException e) {
                            if(optionsText.matches(".*?:\\S.*")){
                                throw new ParseError("Use a space after a semicolon: e.g. {key: value}");
                            } else {
                                throw new ParseError("Invalid property syntax.\nUse something like: {key1: value1, key2, key3: value3}");
                            }
                        }
                    }

                    String[] replies = StringUtil.unquote(replyMatcher.group(4).trim()).split("['\"]?\\s*\\|\\s*['\"]?");
                    List<List<String>> dnf = Arrays.stream(replies)
                            .map(ands -> Arrays.stream(ands.split("['\"]?\\s*&\\s*['\"]?")).map(String::trim).collect(Collectors.toList()))
                            .collect(Collectors.toList());

                    ReplyRule reply;
                    if(head.trim().equals("()")){
                        // default reply: no constraints
                        reply = new ReplyRule(new LabelRule("reply", new Literal[0][0], lineNr), dnf, lineNr);
                    } else {
                        Literal[][] matchRule = compileBody(head, optimizeRuleLogic, normalizer);
                        reply = new ReplyRule(new LabelRule("reply", matchRule, lineNr), dnf, lineNr);
                    }
                    if(props != null)
                        reply.setProps(props);

                    replyList.add(reply);

                }  else {
                    if(!prefixes.isEmpty()){
                        statementLineNr = 1;
                        throw new ParseError("Prefix block syntax is only allowed for reply rules, not when defining labels.\nApplied prefixes: " + prefixes, lineNr);
                    }

                    // match rule
                    LabelRule rule = compileRule(statement, optimizeRuleLogic, normalizer);
                    rule.setLine(statementLineNr);
                    ruleList.add(rule);
                }

                if(closePrefixBlock){
                    prefixes.remove(prefixes.size()-1);
                }
            }
        } catch (ParseError e) {
            e.setLine(statementLineNr + e.getLine()-1); // add line info
            throw e;
        }

        // rewrite reply conditions containing literal searches (non-label refs)
        // int anonymousLabelId = 0;
        for (ReplyRule reply : replyList) {
            boolean doRewrite = false;
            expression: for (Literal[] disjunction : reply.rule.expression) {
                for (Literal disj : disjunction) {
                    if(disj.meta == '='){
                        final Token leftStartToken = disj.tokens[0];
                        final Token rightStartToken = disj.tokens[disj.marker + 1];
                        if((leftStartToken.isMatchOnNormalized() && StringUtil.startsWithUpperCase(leftStartToken.getWord()))
                                || (rightStartToken.isMatchOnNormalized() && StringUtil.startsWithUpperCase(rightStartToken.getWord())))
                        {
                            // NER class
                            doRewrite = true;
                        } else {
                            continue;
                        }
                    }
                    if(disj.meta != '@'){
                        // a text literal
                        doRewrite = true;
                    }

                    if(doRewrite)
                        break expression;

                }
            }
            if(doRewrite){
                // 1. create new rule
                String anoRuleHead = "label" + (reply.line);
                LabelRule anoRule = new LabelRule(anoRuleHead, reply.rule.expression, reply.line);
                anoRule.props.put("within", "all");
                ruleList.add(anoRule);
                // 2. use new anoRule
                reply.rule.expression = new Literal[][]{{Literal.createReference(anoRuleHead)}};
            }
        }

        Script script = new Script(scriptConfig, ruleList, replyList);
        script.setPlugins(nlp.getEntityPlugins(scriptConfig));

        return script;
    }

    /**
     * Compile a single label expression into an evaluable/executable form.
     * @param rule the label of the rule (applied tag)
     * @param simplify whether ti optimize the expression
     * @param normalizer how words are canonicalized
     * @return an internal representation of the label rule
     */
    public static LabelRule compileRule(String rule, boolean simplify, StringNormalizer normalizer){
        Matcher ruleMatcher = RULE_PATTERN.matcher(rule);
        if (ruleMatcher.find()) {
            // rule: (.*?) *([{](.*?)[}])? *= *(.*)
            // gr =    1      2   3              4

            String head = ruleMatcher.group(1).trim();
            String body = ruleMatcher.group(4);
            String optionsText = ruleMatcher.group(3);
            LinkedHashMap<String,Object> props = null;
            if(optionsText != null){
                try {
                    props = OBJECT_MAPPER.readValue(optionsText,  new TypeReference<LinkedHashMap<String,Object>>() {});
                } catch (IOException e) {
                    throw new ParseError("Invalid property syntax.\nUse something like: {key1: value1, key2, key3: value3}");
                }
            }
            LabelRule labelRule = new LabelRule(head, compileBody(body, simplify, normalizer));
            if(props != null)
                labelRule.setProps(props);

            return labelRule;

        } else {
            /*if(Pattern.compile("(?U)^@\\w+ +\\w+.*?<-.*", Pattern.DOTALL).matcher(rule).matches()){
                throw new ParseError("Labels on the left-hand-side should only contains letters, not spaces (@label <- ...)");
            }
            if(Pattern.compile("(?U)^@\\w+_+\\w+.*?<-.*", Pattern.DOTALL).matcher(rule).matches()){
                throw new ParseError("Labels on the left-hand-side should only contains letters, not underscores (@label <- ...)");
            }*/
            if(Pattern.compile("(?U)^[ \t\f]+@\\w+.*?<-.*", Pattern.DOTALL).matcher(rule).matches()){
                throw new ParseError("Don't indent when defining a rule.\nIndentation is used to continue a rule on the next line(s).");
            }
            throw new ParseError("The line does not assign a rule (@label <- ...)");
        }
    }

    /**
     * Compile a single expression into an evaluable/executable form.
     * @param expression the logical expression/body of the rule
     * @return an internal representation of the label rule
     */
    public static Literal[][] compileBody(String expression){
        return compileBody(expression, true, null);
    }

    /**
     * Compile a single expression into an evaluable/executable form.
     * @param expression the logical expression/body of the rule
     * @param simplify whether ti optimize the expression
     * @param normalizer how words are canonicalized
     * @return an internal representation of the label rule
     */
    public static Literal[][] compileBody(String expression, boolean simplify, StringNormalizer normalizer){
        Compiler parser = Parboiled.createParser(Compiler.class);
        ParsingResult<?> result;
        try {
            result = new RecoveringParseRunner(parser.ExpressionLine()).run(expression);
        } catch (Exception e) {
            // The parser may trow IllegalState exceptions (parboiled lib issue?)
            throw new ParseError("The line contains an unknown error", 1);
        }
        if (result.hasErrors()) {
            String message = printParseErrors(result);
            Matcher lineMatcher = Pattern.compile("\\(line (\\d+)").matcher(message);
            int line = 1;
            if(lineMatcher.find()){
                line = Integer.parseInt(lineMatcher.group(1));
            }

            message = message.replaceAll(", expected.*", "..."); // remove internal debug info
            throw new ParseError(message, line);
        }

        Object ast = result.parseTreeRoot.getValue();
        OperatorNode cnfTree = CNFTransform.toCNFTree((OperatorNode) ast);
        ArrayList<ArrayList<Literal>> cnfList = CNFTransform.cnfTreeToCNFList(cnfTree);
        if(simplify){
            cnfList = CNFTransform.simplify(cnfList);
        }

        if(normalizer != null){
            cnfList.forEach(disj -> disj.forEach(lit -> normalizer.normalizeTokens(lit.getTokens())));
        }
        return CNFTransform.cnfListToCNFArray(cnfList);
    }

    org.parboiled.Rule RuleLine() {
        return Sequence(Expression(), EOI);
    }

    org.parboiled.Rule ExpressionLine() {
        return Sequence(ZeroOrMore(AnyOf(" \n\t\f").label("Whitespace")), Expression(), ZeroOrMore(AnyOf(" \n\t\f").label("Whitespace")), EOI);
    }

    org.parboiled.Rule Expression() {
        Var<String> op = new Var<>();
        return Sequence(
                And(),
                ZeroOrMore(
                        // we use a FirstOf(String, String) instead of a AnyOf(String) so we can use the
                        // fromStringLiteral transformation (see below), which automatically consumes trailing whitespace
                        FirstOf("| ", "OR "), op.set(matchOrDefault("|")),
                        And(),
                        push(new OperatorNode("|", pop(1), pop()))
                )
        );
    }

    org.parboiled.Rule And() {
        Var<String> op = new Var<>();
        return Sequence(
                Atom(),
                ZeroOrMore(
                        FirstOf("& ", "AND "), op.set(matchOrDefault(("&"))),
                        Atom(),
                        push(new OperatorNode("&", pop(1), pop()))
                )
        );
    }

    org.parboiled.Rule Concat() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf(SemicolonQuotedString(), DoubleQuotedString(), SingleQuotedString(), AnyOne(), AnyOneOrMore(), AnyZeroOrMore(), UnquotedString()),
                ZeroOrMore(
                        "_ ", op.set(matchOrDefault("_")),
                        FirstOf(SemicolonQuotedString(), DoubleQuotedString(), SingleQuotedString(), AnyOne(), AnyOneOrMore(), AnyZeroOrMore(), UnquotedString()),
                        push(new OperatorNode("_", pop(1), pop()))
                )
        );
    }

    org.parboiled.Rule Compare() {
        Var<String> op = new Var<>();
        return Sequence(
                CompareOperand(),
                FirstOf( "<=", ">=", "==", "!=", "<", ">"), op.set(matchOrDefault((""))), WhiteSpace(),
                CompareOperand(),
                push(new OperatorNode("=", pop(1), pop()).setValue(op.get()))
        );
    }

    org.parboiled.Rule CompareOperand() {
        return FirstOf(PropertyReference(), PropertyClass(), Number(), DoubleQuotedString(), SingleQuotedString(), UnquotedString());
    }

    org.parboiled.Rule Atom() {
        return FirstOf(Not(), Compare(), Reference(), Parens(), Concat());
    }


    org.parboiled.Rule Not() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf("- ", "NOT "), op.set(matchOrDefault("-")),
                Atom(),
                push(new OperatorNode("-", pop(), null))
        );
    }

    org.parboiled.Rule Reference() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf("@", "REFERENCE "), op.set(matchOrDefault("@")),
                UnquotedString(),
                push(new OperatorNode("@", pop(), null))
        );
    }

    org.parboiled.Rule PropertyReference() {
        Var<String> op = new Var<>();
        return Sequence(
                FirstOf("@", "REFERENCE "), op.set(matchOrDefault("@")),
                Sequence(LetterSequence(),PERIOD,LetterSequence()),
                push(new OperatorNode("@", new OperatorNode("A", new OperatorNode("@"+matchOrDefault("")), null), null)),
                WhiteSpace()
        );
    }
    org.parboiled.Rule PropertyClass() {
        return Sequence(
                Sequence(LetterSequenceUppercase(),PERIOD,LetterSequence()),
                push(new OperatorNode("A", new OperatorNode(matchOrDefault(null)), null)),
                WhiteSpace()
        );
    }

    org.parboiled.Rule Parens() {
        return Sequence("( ", Expression(), ") ");
    }

    org.parboiled.Rule AnyOne() {
        return Sequence("?", push(new OperatorNode("?")), WhiteSpace());
    }

    org.parboiled.Rule AnyOneOrMore() {
        return Sequence("+", push(new OperatorNode("+")), WhiteSpace());
    }

    org.parboiled.Rule AnyZeroOrMore() {
        return Sequence("*", push(new OperatorNode("*")), WhiteSpace());
    }

    org.parboiled.Rule Number() {
        return Sequence(
                // we use another Sequence in the "Number" Sequence so we can easily access the input text matched
                // by the three enclosed rules with "match()" or "matchOrDefault()"
                Sequence(
                        Optional("-"),
                        OneOrMore(Digit()),
                        Optional(".", OneOrMore(Digit()))
                ),

                // the matchOrDefault() call returns the matched input text of the immediately preceding rule
                // or a default string (in this case if it is run during error recovery (resynchronization))
                push(new OperatorNode("1", new OperatorNode(matchOrDefault("0")), null)),
                WhiteSpace()
        );
    }

    org.parboiled.Rule Digit() {
        return CharRange('0', '9');
    }

    org.parboiled.Rule WhiteSpace() {
        return
                ZeroOrMore(
                        FirstOf(
                                AnyOf(" \t\f").label("Whitespace"),
                                Sequence(Newline(), OneOrMore(AnyOf(" \t\f"))).label("Whitespace")
                        )
                );
    }

    org.parboiled.Rule Newline() {
        return FirstOf('\n', Sequence('\r', Optional('\n')));
    }

    org.parboiled.Rule QuotedStrNormal(String escapeSeq) {
        return NoneOf(escapeSeq);
    }

    org.parboiled.Rule QuotedStrSpecial(String escapeSeq) {
        return String(escapeSeq);
    }

    org.parboiled.Rule QuotedStrNSN(String escapeSeq) {
        return Sequence(Sequence(
                ZeroOrMore(QuotedStrNormal(escapeSeq)),
                ZeroOrMore(QuotedStrSpecial(escapeSeq), ZeroOrMore(QuotedStrNormal(escapeSeq)))
        ), push(new OperatorNode(matchOrDefault(""))));
    }

    org.parboiled.Rule SingleQuotedString() {
        return Sequence('\'', QuotedStrNSN("\\\'"), '\'', WhiteSpace(), push(new OperatorNode("A", pop(), null)));
    }

    org.parboiled.Rule DoubleQuotedString() {
        return Sequence('"', QuotedStrNSN("\\\""), '"', WhiteSpace(), push(new OperatorNode("E", pop(), null)));
    }

    org.parboiled.Rule SemicolonQuotedString() {
        return Sequence(':', QuotedStrNSN("\\:"), ':', WhiteSpace(), push(new OperatorNode(":", pop(), null)));
    }

    org.parboiled.Rule UnquotedString() {
        return Sequence(
                LetterSequence(),
                push(new OperatorNode("A", new OperatorNode(matchOrDefault("")), null)), WhiteSpace());
    }

    org.parboiled.Rule LetterSequence() {
        return OneOrMore(FirstOf(
                CharRange("Basic Latin Uppercase"),
                CharRange("Basic Latin Lowercase"),
                Ch('\'' ), // e.g. I'm
                Ch('-' ), // e.g. lower-case
                CharRange("Basic Latin Accents"),
                CharRange("Digits"),
                CharRange("Arabic"),
                CharRange("Arabic Supplement"),
                CharRange("Greek and Coptic"),
                CharRange("Cyrillic"),
                CharRange("Hebrew")
        ));
    }

    org.parboiled.Rule LetterSequenceUppercase() {
        return OneOrMore(FirstOf(
                CharRange("Basic Latin Uppercase"),
                Ch('-' ), // e.g. lower-case
                CharRange("Digits")
        ));
    }

    org.parboiled.Rule CharRange(String name){
        char[] chars = CharRangeUtil.RANGES.get(name);
        return CharRange(chars[0], chars[1]);
    }

    org.parboiled.Rule SimplePredicate() {
        return Sequence(
                UnquotedString(),
                Optional(LPAR, Optional(
                        UnquotedString(),
                        ZeroOrMore(COMMA, UnquotedString())
                ), RPAR)
        );
    }

    org.parboiled.Rule Terminal(String string) {
        return Sequence(string, WhiteSpace()).label('\'' + string + '\'');
    }

    final org.parboiled.Rule LPAR = Terminal("(");
    final org.parboiled.Rule RPAR = Terminal(")");
    final org.parboiled.Rule COMMA = Terminal(",");
    final org.parboiled.Rule PERIOD = Terminal(".");

    final org.parboiled.Rule LE = Terminal("<=");
    final org.parboiled.Rule LT = Terminal("<");
    final org.parboiled.Rule EQ = Terminal("==");
    final org.parboiled.Rule NEQ = Terminal("!=");
    final org.parboiled.Rule GT = Terminal(">");
    final org.parboiled.Rule GE = Terminal(">=");


    // we redefine the rule creation for string literals to automatically match trailing whitespace if the string
    // literal ends with a space character, this way we don't have to insert extra whitespace() rules after each
    // character or string literal

    @Override
    protected org.parboiled.Rule fromStringLiteral(String string) {
        return string.endsWith(" ") ?
                Sequence(String(string.substring(0, string.length() - 1)), WhiteSpace()) :
                String(string);
    }

    /**
     * Generalize all word instances in a script, by adding variants (e.g. hypernyms/synonyms)
     * @param script original script
     * @param generalizer the way of deriving relevant words from a single word
     * @return a new script that matches more often
     */
    public static Script generalize(Script script, Generalizer generalizer){
        Map<String, List<LabelRule>> rules = script.rules;
        List<LabelRule> extRules = new ArrayList<>();
        for (String label : rules.keySet()) {
            for (LabelRule rule : rules.get(label)) {
                Literal[][] expression = rule.expression;
                Literal[][] extExpression = new Literal[expression.length][];
                for (int i = 0, expressionLength = expression.length; i < expressionLength; i++) {
                    Literal[] disjunction = expression[i];
                    List<Literal> extDisjunction = new ArrayList<>();
                    for (Literal literal : disjunction) {
                        extDisjunction.add(literal);
                        if (literal.meta != 'a') {
                            continue;
                        }
                        Collection<Token[]> extension = generalizer.extend(literal.tokens);
                        for (Token[] tokens : extension) {
                            extDisjunction.add(new Literal(tokens, literal.negated, literal.meta));
                        }
                    }
                    extExpression[i] = extDisjunction.toArray(new Literal[0]);

                }
                extRules.add(new LabelRule(rule.head, extExpression, rule.line));
            }
        }
        return new Script(script.config, extRules, script.replies).setPlugins(script.plugins);

    }

    /**
     * Syntax error with line location
     */
    public static class ParseError extends IllegalArgumentException{
        String message;
        int line;



        public ParseError(String message, int line) {
            this.message = message;
            this.line = line;
        }

        public ParseError(String message) {
            this.message = message;
            this.line = 1;
        }

        public ParseError(IOException yamlError){
            int line = 1;
            Matcher matcher = Pattern.compile("\\bline (\\d+)", Pattern.MULTILINE).matcher(yamlError.getMessage());
            if(matcher.find()){
                line = Integer.parseInt(matcher.group(1));
            }
            message = "There is an error in this YAML configuration line.\n" +yamlError.getMessage();
            this.line = line;
        }

        public ParseError(Exception e){
            this(e.getMessage(), 1);
        }

        @Override
        public String getMessage() {
            return message;
        }

        public ParseError setMessage(String message) {
            this.message = message;
            return this;
        }

        public int getLine() {
            return line;
        }

        public ParseError setLine(int line) {
            this.line = line;
            return this;
        }

        @Override
        public String toString() {
            return "ParseError{" +
                    "message='" + message + '\'' +
                    ", line=" + line +
                    '}';
        }
    }

    /**
     * CLI Interactive mode for expressions
     * @param args ignored
     */
    public static void main(String[] args) {
        Compiler parser = Parboiled.createParser(Compiler.class);

        while (true) {
            System.out.print("Enter a match expression (single RETURN to exit)!\n");
            String input = new Scanner(System.in).nextLine();
            if (StringUtils.isEmpty(input)) break;

            ParsingResult<?> result = new RecoveringParseRunner(parser.ExpressionLine()).run(input);

            if (result.hasErrors()) {
                System.out.println("\nParse Errors:\n" + printParseErrors(result));
            } else {
                Object value = result.parseTreeRoot.getValue();
                if (value != null) {
                    String str = value.toString();
                    int ix = str.indexOf('|');
                    if (ix >= 0) str = str.substring(ix + 2); // extract value part of AST node toString()
                    System.out.println(input + " = " + str + '\n');
                }
                if (value instanceof GraphNode) {
                    System.out.println("\nAbstract Syntax Tree:\n" +
                            printTree((GraphNode) value, new ToStringFormatter(null)) + '\n');

                    OperatorNode cnfTree = CNFTransform.toCNFTree((OperatorNode) value);
                    System.out.println("CNF Abstract Syntax Tree:\n" +
                            printTree(cnfTree, new ToStringFormatter(null)) + '\n');

                    ArrayList<ArrayList<Literal>> cnfList = CNFTransform.cnfTreeToCNFList(cnfTree);
                    System.out.println("CNF[][]        = " + cnfList);
                    cnfList = CNFTransform.simplify(cnfList);
                    System.out.println("CNF[][] simple = " + cnfList);

                    Literal[][] cnfArray = CNFTransform.cnfListToCNFArray(cnfList);
                    String luceneQuery = new LuceneConverter().convert(cnfArray);
                    System.out.println("Lucene = " + luceneQuery);
                    String postgreSQL = new PostgresConverter().convert(cnfArray);
                    System.out.println("PostgreSQL = " + postgreSQL);

                } else {
                    System.out.println("\nParse Tree:\n" + printNodeTree(result) + "\n");
                }
            }
        }
    }

}