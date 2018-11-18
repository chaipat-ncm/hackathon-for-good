package org.c4i.nlp.match;

import com.google.common.collect.ImmutableSet;
import org.c4i.nlp.Nlp;
import org.c4i.util.ReverseIterator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Possible replies for when a rule matches.
 * @author Arvid Halma
 * @version 6-7-17
 */
public class ReplyRule extends Rule{
    LabelRule rule;
    List<List<String>> replies;

    protected static final Map<String, Set<String>> VALID_PROPS = new HashMap<>();
    static {
        VALID_PROPS.put("continue", null);
        VALID_PROPS.put("repeat", null);
        VALID_PROPS.put("within", ImmutableSet.of("last", "all", "NUMBER"));
        VALID_PROPS.put("addText", ImmutableSet.of("STRING"));
        VALID_PROPS.put("addLabel", ImmutableSet.of("STRING", "LIST"));
        VALID_PROPS.put("removeLabel", ImmutableSet.of("STRING", "LIST"));
        VALID_PROPS.put("reflect", null);
    }

    Pattern LABEL_VAR = Pattern.compile("@(\"?)(\\w+?)(\\.(\\w+?))?\"?\\b");
    Pattern USER_VAR = Pattern.compile("\\$(\\w+?)\\b");

    public ReplyRule(LabelRule rule, List<List<String>> replies) {
        this.rule = rule;
        this.replies = replies;
    }

    public ReplyRule(LabelRule rule, List<List<String>> replies, int line) {
        this.rule = rule;
        this.replies = replies;
        this.line = line;
    }

    @Override
    public Map<String, Set<String>> validProps() {
        return VALID_PROPS;
    }

    public LabelRule getRule() {
        return rule;
    }

    public ReplyRule setRule(LabelRule rule) {
        this.rule = rule;
        return this;
    }

    public List<List<String>> getReplies() {
        return replies;
    }

    public ReplyRule setReplies(List<List<String>> replies) {
        this.replies = replies;
        return this;
    }

    public List<String> randomReply(){
        return replies.get((int)Math.floor(replies.size() * Math.random()));
    }

    public List<String> randomReply(Script script, String text, List<Range> eval, Nlp nlp) {
        List<String> result = new ArrayList<>();

        List<String> templates = randomReply();
        for (String template : templates) {
            Matcher labelVarMatcher = LABEL_VAR.matcher(template);
            StringBuffer sb = new StringBuffer();
            nextVar:
            while (labelVarMatcher.find()) {
                boolean normalize = !labelVarMatcher.group(1).equals("\"");
                String var = labelVarMatcher.group(2);
                String prop = labelVarMatcher.group(4);
                for (Range range : new ReverseIterator<>(eval)) { // reverse: latest value is considered more relevant
                    if (
                            range.label.equals(var) // requested var matches
                            || (var.startsWith("label") // anonymous label
                                        && !isFallback() // non-empty expression
                                        && range.label.equals(this.rule.expression[0][0].tokens[0].getWord())) // this specific anonymous label
                            )
                    {
                        // replace variable
                        String replacement = prop != null ? range.props.getOrDefault(prop, prop): text.substring(range.charStart, range.charEnd);

                        // possibly reflect the reply as well
                        if(isProp("reflect", script.getConfig().getRuleProperties().getReply())){
                            replacement = nlp.getSubstitution(script.getConfig()).apply(replacement);
                        }

                        labelVarMatcher.appendReplacement(sb, Matcher.quoteReplacement(normalize ? replacement.toLowerCase() : replacement));
                        continue nextVar;
                    }
                }
            }
            labelVarMatcher.appendTail(sb);

            String finalReply = nlp.getReplyVariables().apply(sb.toString());

            result.add(finalReply);
        }


        return result;
    }

    public boolean isFallback(){
        return rule.expression.length == 0; // no constraints
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplyRule)) return false;

        ReplyRule that = (ReplyRule) o;

        return line == that.line;
    }

    @Override
    public int hashCode() {
        return line;
    }

    @Override
    public String toString() {
        return CNFTransform.toString(rule.expression)  + " "
                + propertyString() +"-> "
                + replies.stream()
                    .map(ands -> String.join(" & ", ands))
                    .collect(Collectors.joining(" | "));
    }
}
