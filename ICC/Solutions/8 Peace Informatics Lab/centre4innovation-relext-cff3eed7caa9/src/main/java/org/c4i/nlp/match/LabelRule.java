package org.c4i.nlp.match;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A trigger rule: when the expression (body) matches, the label (head) is triggered.
 * @author Arvid Halma
 * @version 13-4-2017 - 20:52
 */
public class LabelRule extends Rule{

    String head;
    Literal[][] expression;

    protected static final Map<String, Set<String>> VALID_PROPS = new HashMap<>();
    static {
        VALID_PROPS.put("within", ImmutableSet.of("all", "sentence", "NUMBER"));
        VALID_PROPS.put("match", ImmutableSet.of("all", "first"));
        VALID_PROPS.put("set", ImmutableSet.of("MAP"));
    }

    public LabelRule(String head, Literal[][] expression) {
        this.head = head;
        this.expression = expression;
    }

    public LabelRule(String head, Literal[][] expression, int line) {
        this.head = head;
        this.expression = expression;
        this.line = line;
    }

    @Override
    public Map<String, Set<String>> validProps() {
        return VALID_PROPS;
    }

    public String getHead() {
        return head;
    }

    public LabelRule setHead(String head) {
        this.head = head;
        return this;
    }

    public Literal[][] getExpression() {
        return expression;
    }

    public LabelRule setExpression(Literal[][] expression) {
        this.expression = expression;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabelRule)) return false;

        LabelRule rule = (LabelRule) o;

        if (head != null ? !head.equals(rule.head) : rule.head != null) return false;
        return Arrays.deepEquals(expression, rule.expression);
    }

    @Override
    public int hashCode() {
        int result = head != null ? head.hashCode() : 0;
        result = 31 * result + Arrays.deepHashCode(expression);
        return result;
    }

    @Override
    public String toString() {
        return "@" + head + " " + propertyString() +"<- " + CNFTransform.toString(expression);
    }
}