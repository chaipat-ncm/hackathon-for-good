package org.c4i.nlp.match;

import org.parboiled.trees.ImmutableBinaryTreeNode;

import java.util.Objects;

/**
 * The AST node for the calculators. The type of the node is carried as a Character that can either contain
 * an operator char or be null. In the latter case the AST node is a leaf directly containing a value.
 */
public class OperatorNode extends ImmutableBinaryTreeNode<OperatorNode> {
    String value;
    String operator;

    public OperatorNode(String value) {
        super(null, null);
        this.value = value;
    }

    public OperatorNode(String operator, OperatorNode left, OperatorNode right) {
        super(left, right);
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public OperatorNode setValue(String value) {
        this.value = value;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isLeaf(){
        return operator == null;
    }

    public boolean isOperator(String op){
        return Objects.equals(operator, op);
    }

    public boolean leftIsOperator(String op){
        OperatorNode left = left();
        return left != null && Objects.equals(left.operator, op);
    }

    public boolean rightIsOperator(String op){
        OperatorNode right = right();
        return right != null && Objects.equals(right.operator, op);
    }

    public boolean hasChildOperator(String op){
        return leftIsOperator(op) || rightIsOperator(op);
    }

    public String eval() {
        if (operator == null) return value;
        switch (operator) {
            case "_":
                return left().eval() + "_" + right().eval();
            case "&":
                return "(" + left().eval() + " AND " + right().eval() + ")";
            case "|":
                return "(" + left().eval() + " OR " + right().eval() + ")";
            case "=":
                return left().eval() + " "+value+" " + right().eval();
            case ":":
                return ":" + (left().eval()) + ":";
            case "E":
                return "\"" + left().eval() +"\"";
            case "A":
                return "'" + (left().eval()) + "'";
            case "1":
                return left().eval();
            case "-":
                return "NOT(" + left().eval() + ")";
            case "?": return "?";
            case "+": return "+";
            case "*": return "*";
            case "@": return "@";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return (isLeaf() ? "Value " + value : "Operator '" + operator + "'") + " : " + eval();
    }
}
