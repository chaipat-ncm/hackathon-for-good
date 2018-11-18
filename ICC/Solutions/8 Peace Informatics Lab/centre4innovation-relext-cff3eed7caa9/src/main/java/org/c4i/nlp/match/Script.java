package org.c4i.nlp.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.StringUtils;
import org.parboiled.common.ImmutableList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of rules, that defines labels that are triggered under the given conditions.
 * Also replies can be defined to respond to the detected labels in a text.
 *
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class Script {
    ScriptConfig config;
    final Map<String, List<LabelRule>> rules;
    final List<ReplyRule> replies;

    List<EntityPlugin> plugins;

    private final Set<String> allDefinedLabels = new HashSet<>();

    private static final Pattern REPLY_TEMPLATE_REF = Pattern.compile("(?U)@(\\w+?)\\b.");


    public Script() {
        config = new ScriptConfig();
        rules = new LinkedHashMap<>();
        replies = new ArrayList<>();
        plugins = new ArrayList<>();
    }

    public Script(Script script) {
        this(script.config, script.rules.values().stream().collect(ArrayList::new, List::addAll, List::addAll), script.replies);
        this.plugins = script.plugins;
    }

    public Script(ScriptConfig config, Collection<LabelRule> rules, List<ReplyRule> replies) {
        this();
        this.config = config == null ? new ScriptConfig() : config;

        for (LabelRule rule : rules) {
            if(!this.rules.containsKey(rule.head)){
                this.rules.put(rule.head, new ArrayList<>(1));
            }
            this.rules.get(rule.head).add(rule);
        }
        this.replies.addAll(replies);
        plugins = new ArrayList<>();

        validate();
    }

    public ScriptConfig getConfig() {
        return config;
    }

    public Map<String, List<LabelRule>> getRules() {
        return rules;
    }

    public List<ReplyRule> getReplies() {
        return replies;
    }

    public List<EntityPlugin> getPlugins() {
        return plugins;
    }

    public Script setPlugins(List<EntityPlugin> plugins) {
        this.plugins = plugins;
        return this;
    }

    public void validate(){
        // gather all labels that are possibly defined in the script
        allDefinedLabels.clear();
        // add label rule heads
        allDefinedLabels.addAll(rules.keySet());
        // add labels from "addLabel" in reply props
        for (ReplyRule reply : replies) {
            Object addLabel = reply.getProp("addLabel");
            if(addLabel == null)
                continue;
            if(addLabel instanceof String)
                allDefinedLabels.add((String) addLabel);

            if(addLabel instanceof List){
                ((List)addLabel).forEach(obj -> allDefinedLabels.add(obj.toString()));
            }
        }

        // start checking...

        // references to labels
        checkLabelReferences();
        checkReplyReferences();
        checkReplyTemplateReferences();

        // recursion
        checkDirectRecursion();
        checkCyclicRecursion();

        checkUnreachableReplyRules();

        // properties
        checkProperties();

        // illegal prop combos
        checkReplyRuleContinueAddTextCombinations();
        checkReplyRuleContinueRepeatCombinations();
    }

    private void checkSpaces(){
        // deprecated check: 'foo bar' is now automatically coverted to: 'foo'_'bar'
        rules.values().stream().flatMap(Collection::stream).forEach(rule -> Arrays.stream(rule.expression).flatMap(Arrays::stream).forEach(lit -> {
            if (lit.meta == 'a' && Arrays.stream(lit.tokens).anyMatch(t -> t.getWord().contains(" "))) {
                throw new Compiler.ParseError(String.format("Rule '@%s' contains a term that contains spaces, but the sequence operator _ should be used instead.", rule.head), rule.line);
            }
        }));
    }

    private void checkReplyRuleComparisons(){
        for (ReplyRule reply : replies) {
            for (Literal[] literals : reply.rule.expression) {
                for (Literal literal : literals) {
                    if (literal.meta == '=') {
                        if(Character.isUpperCase(literal.tokens[0].getWord().charAt(0)) || Character.isUpperCase(literal.tokens[2].getWord().charAt(0))){
                            throw new Compiler.ParseError("Replies can't have comparisons with NER classes, only with @references.", reply.line);
                        }
                    }
                }
            }
        }

    }

    private void checkLabelReferences() {
        rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
            checkReferences(rule.expression, rule.line);
        });
    }

    private void checkReplyReferences() {
        for (ReplyRule rule : replies) {
            checkReferences(rule.rule.expression, rule.line);
        }
    }

    private void checkReferences(Literal[][] expression, int line) {

        Arrays.stream(expression).flatMap(Arrays::stream).forEach(lit -> {
            List<String> refsToCheck;

            switch (lit.meta){
                case '@': refsToCheck = ImmutableList.of(lit.tokens[0].getWord()); break;
                case '=': {
                    refsToCheck = new ArrayList<>(2);
                    String left = getLabelPart(lit.tokens[0].getWord());
                    if(left != null) refsToCheck.add(left);
                    String right = getLabelPart(lit.tokens[2].getWord());
                    if(right != null) refsToCheck.add(right);
                } break;
                default : refsToCheck = ImmutableList.of();
            }

            for (String ref: refsToCheck){
                if (!allDefinedLabels.contains(ref)) {
                    if(ref.startsWith("@")){
                        throw new Compiler.ParseError(String.format("When adding labels, don't use '@', just: %s", ref.substring(1)), line);
                    }

                    if(StringUtils.isAllUpperCase(ref)){
                        throw new Compiler.ParseError(String.format("The label @%s is not defined.\nMaybe remove '@' if you want reference a data sheet (just: %s).", ref, ref), line);
                    }

                    for (String definedLabel : allDefinedLabels) {
                        if(definedLabel.equalsIgnoreCase(ref)){
                            throw new Compiler.ParseError(String.format("References to labels are case sensitive. Use: @%s", definedLabel), line);
                        }
                    }

                    throw new Compiler.ParseError(String.format("There is a reference to a label that is not defined: @%s", ref), line);

                }
            }
        });
    }

    private static String getLabelPart(String compareOperand){
        if(compareOperand ==  null || !compareOperand.startsWith("@"))
            return null;
        int propSep = compareOperand.indexOf('.');
        if(propSep < 2){
            return null;
        }
        return compareOperand.substring(1, propSep);
    }

    private void checkReplyTemplateReferences(){
        for (ReplyRule reply : replies) {
            reply.replies.stream().flatMap(Collection::stream).forEach(msg -> {
                Matcher matcher = REPLY_TEMPLATE_REF.matcher(msg);
                while (matcher.find()){
                    String label = matcher.group(1);
                    if("label".equals(label))
                        continue;

                    if(!allDefinedLabels.contains(label)) {
                        for (String definedLabel : allDefinedLabels) {
                            if (definedLabel.equalsIgnoreCase(label)) {
                                throw new Compiler.ParseError(String.format("References to labels are case sensitive. Use: @%s", definedLabel), reply.line);
                            }
                        }

                        throw new Compiler.ParseError(String.format("There is a reference to a label that is not defined: @%s", label), reply.line);
                    }
                }
            });
        }
    }

    private void checkProperties(){
        checkLabelRuleProperties();
        checkReplyRuleProperties();
    }

    private void checkReplyRuleContinueAddTextCombinations(){
        for (ReplyRule reply : replies) {
            if(reply.props.containsKey("continue") && reply.props.containsKey("addText")){
                throw new Compiler.ParseError("The reply properties 'continue' and 'addText' can't be combined, It only doesn't works for a chain of continuations.", reply.line);
            }
        }
    }

    private void checkReplyRuleContinueRepeatCombinations(){
        for (ReplyRule reply : replies) {
            if(reply.props.containsKey("continue") && reply.props.containsKey("repeat")){
                throw new Compiler.ParseError("The reply properties 'continue' and 'repeat' can't be combined, because of potential infinite loops.", reply.line);
            }
        }
    }

    private void checkRuleProperties(Collection<? extends Rule> rules){
        for (Rule rule : rules) {
            for (Map.Entry<String, Object> prop : rule.props.entrySet()) {
                String key = prop.getKey();

                if(!rule.isValidProp(key)){
                    if(rule.isValidProp(key.toLowerCase())){
                        throw new Compiler.ParseError(String.format("Properties are case sensitive. Use {%s: ...}'.", key.toLowerCase()), rule.line);
                    }
                    throw new Compiler.ParseError(String.format("This is not a valid property '%s'.", key), rule.line);
                }
                Object val = prop.getValue();
                if(!rule.isValidPropValue(key, val)){
                    final String valStr = val.toString();
                    Set<String> vals = rule.validPropValues(key);
                    if(vals == null){
                        throw new Compiler.ParseError(String.format("The property '%s' doesn't take any arguments (remove the ': %s' part)", key, val), rule.line);
                    } else {
                        if(rule.isValidPropValue(key, (valStr).toLowerCase())){
                            throw new Compiler.ParseError(String.format("Property values are case sensitive. Use {%s, %s}.", key, valStr.toLowerCase()), rule.line);
                        }
                        throw new Compiler.ParseError(String.format("The value of property '%s' is not valid (use one of the values: %s)", key, vals.toString()), rule.line);
                    }
                }
            }
        }
    }

    private void checkLabelRuleProperties(){
        checkRuleProperties(rules.values().stream().collect(ArrayList::new, List::addAll, List::addAll));
    }

    private void checkReplyRuleProperties(){
        checkRuleProperties(replies);
    }

    @Deprecated
    private void checkReplyPureRuleReferences(){
        // expressions should only contain references to rule heads (no string literal/patterns)
        for (ReplyRule reply : replies) {
            for (Literal[] literals : reply.rule.expression) {
                for (Literal literal : literals) {
                    if(literal.meta == '='){
                        continue;
                    }
                    String word = literal.getTokens()[0].getWord();
                    if(literal.meta != '@'){
                        throw new Compiler.ParseError(String.format("The reply expression should only contain references (@some_label), not: %s.", word), reply.line);
                    }
                    if(!rules.containsKey(word)){
                        throw new Compiler.ParseError(String.format("The reply refers to a rule that is not defined: %s.", word), reply.line);
                    }
                }
            }
        }
    }

    private void checkFacebookLimits(){
        /*
        There is a limit for the number of elements and buttons you can add to the templates.
        There is also a limit for the length of messages and number of captions.
        Upon exceeding these limits, instead of having your text trimmed, you will receive an
        error and the API will (in some cases) refuse to send your message.

        Maximum length of characters is 80 for titles and subtitles (descriptions),
        and 320 for text messages. You can have up to 10 items among your generic templates
        with 3 buttons for each maximum. For quick messages, you can have up to 11 different buttons,
        but each of them must consist of 20 characters maximum.

        Source: https://chatbotsmagazine.com/listicle-of-things-missing-from-facebook-messenger-chatbot-platforms-documentation-d1d50922ef15
        */

        Pattern button = Pattern.compile("\\bBUTTON\\( *(.*?) *, *(.*?) *\\)");
        for (ReplyRule reply : replies) {
            reply.replies.stream().flatMap(List::stream).forEach(text -> {
                Matcher buttonMatcher = button.matcher(text);
                int buttonCount = 0;
                while(buttonMatcher.find()){
                    buttonCount++;
                    String buttonText = buttonMatcher.group(1);
                    //String buttonValue = buttonMatcher.group(2);
                    if(buttonText.length() > 20){
                        throw new Compiler.ParseError(String.format("The text for the quick reply button is too long (max 20 characters): '%s'", buttonText), reply.line);
                    }
                }
                if(buttonCount > 11){
                    throw new Compiler.ParseError(String.format("There are %d quick reply buttons defined. The maximum is 11.", buttonCount), reply.line);
                }

                if(buttonCount > 0)
                    return;

                if(text.length() > 320){
                    throw new Compiler.ParseError(String.format("The reply text is too long: %d characters. The maximum is 11.", text.length()), reply.line);
                }


            });
        }

    }

    private void checkUnreachableReplyRules(){
        // no matches after reply(){repeat} = ... rules (it always matches)
        for (int i = 0; i < replies.size(); i++) {
            ReplyRule reply = replies.get(i);
            if(reply.isFallback() && reply.isProp("repeat") && i < replies.size()-1){
                throw new Compiler.ParseError("This reply will always keep matching.\nThe replies defined below will never be reached.", reply.line);
            }
        }
    }

    private void checkDirectRecursion() {
        rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
            Literal[][] expression = rule.expression;
            Arrays.stream(expression).flatMap(Arrays::stream).forEach(lit -> {
                String ref = lit.tokens[0].getWord();
                if (lit.meta == '@' && rule.head.equals(ref)) {
                    throw new Compiler.ParseError(String.format("Rule '@%s' contains a reference to itself. No recursion allowed.", ref), rule.line);
                }
            });
        });
    }

    public Map<String, List<Dependency>> dependencyGraph(){
        Map<String, List<Dependency>> graph = new HashMap<>();
        rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
            Arrays.stream(rule.expression).flatMap(Arrays::stream).forEach(lit -> {
                String ref = lit.tokens[0].getWord();
                if(lit.meta == '@') {
                    List<Dependency> edgeList = graph.getOrDefault(ref, new ArrayList<>());
                    edgeList.add(new Dependency(ref, rule.head));
                    graph.put(ref, edgeList);

                }
            });
        });
        return graph;
    }


    public Set<DependencyNode> dependencyTree(){
        List<Dependency> edgeList = dependencyGraph().values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        Set<String> heads = rules.keySet();

        Set<DependencyNode> result  = new LinkedHashSet<>();
        for (String head : heads) {
            result.addAll(dependencyTree(edgeList, head));
        }
        return result;
    }

    public Set<DependencyNode> dependencyTree(List<Dependency> edgeList, String label){
        Set<DependencyNode> nodes = new LinkedHashSet<>();
        Set<Dependency> deps = edgeList.stream().filter(dep -> Objects.equals(dep.to, label)).collect(Collectors.toSet());
        if(deps.isEmpty()){
            nodes.add(new DependencyNode(label));
        } else {
            DependencyNode node = new DependencyNode(label);
            for (Dependency dep : deps) {
                node.children.addAll(dependencyTree(edgeList, dep.from));
            }
            nodes.add(node);
        }

        return nodes;
    }


    private void checkCyclicRecursion(){
        // build graph
        Map<String, List<Dependency>> graph = dependencyGraph();

        for (String v1 : graph.keySet()) {
            for (String v2 : graph.keySet()) {
                if(!v1.equals(v2) && Dependency.reachable(v1, v2, graph) && Dependency.reachable(v2, v1, graph)){
                    // two-way dependencies
                    throw new Compiler.ParseError(String.format("Rules '@%s' and '@%s' are defined in terms of each other.\nNo direct or cyclic recursion allowed when defining rules.", v1, v2), rules.get(v1).get(0).line);
                }
            }
        }
    }


    /**
     * A link between a rule that has a reference to another rule, used in its expression.
     */
    public static class Dependency {
        String from, to;

        public Dependency(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public static boolean reachable(final String src, final String dst, final Map<String, List<Dependency>> graph){
            return reachable(src, dst, graph, new HashSet<>());
        }

        public static boolean reachable(final String src, final String dst, final Map<String, List<Dependency>> graph, final Set<String> visited){
            if(!graph.containsKey(src))
                return false;
            List<Dependency> edgeList = graph.get(src);
            if(edgeList == null)
                return false;

            // depth first search
            for (Dependency edge : edgeList) {
                boolean reach;
                if (dst.equals(edge.to))
                    reach = true;
                else {
                    if(visited.contains(edge.to)){
                        return false;
                    }
                    visited.add(edge.to);
                    reach = reachable(edge.to, dst, graph, visited);
                }

                if (reach) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dependency that = (Dependency) o;
            return Objects.equals(from, that.from) &&
                    Objects.equals(to, that.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    public static class DependencyNode {
        String label;
        Set<DependencyNode> children;

        public DependencyNode(String label) {
            this.label = label;
            this.children = new LinkedHashSet<>(2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DependencyNode that = (DependencyNode) o;
            return Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }

        @Override
        public String toString() {
            return label + ":"+ children;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Script)) return false;

        Script ruleSet = (Script) o;

        if (config != null ? !config.equals(ruleSet.config) : ruleSet.config != null) return false;
        if (rules != null ? !rules.equals(ruleSet.rules) : ruleSet.rules != null) return false;
        return replies != null ? replies.equals(ruleSet.replies) : ruleSet.replies == null;
    }

    @Override
    public int hashCode() {
        int result = config != null ? config.hashCode() : 0;
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        result = 31 * result + (replies != null ? replies.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            String yaml = mapper.writeValueAsString(config);
            result.append(yaml);
            result.append("\n---\n");
        } catch (JsonProcessingException ignored) {
            // unusable config
        }

        rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
            result.append(rule).append('\n');
        });

        result.append('\n');
        replies.forEach(reply -> result.append(reply).append('\n'));

        return result.toString();
    }
}
