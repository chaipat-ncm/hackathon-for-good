package org.c4i.nlp.match;


import org.c4i.nlp.tokenize.Token;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Transform logical expressions to Conjunctive Normal Form (CNF)
 * Every propositional formula can be converted into an equivalent formula that is in CNF.
 * This transformation is based on rules about logical equivalences: the double negative law, De Morgan's laws, and the distributive law.
 * Since all logical formulae can be converted into an equivalent formula in conjunctive normal form, proofs are often based on the assumption that all formulae are CNF.
 * Also evaluating expressions can be done uniformly and efficiently.
 *
 * @author Arvid Halma
 * @version 4/5/13, 9:01 PM
 *
 */
public class CNFTransform {
    private static final String AND = "&", OR  = "|", NOT = "-";
    private static Pattern SPACES = Pattern.compile(" +");

    /**
     * Every propositional formula can be converted into an equivalent formula that is in CNF.
     * @param tree some logical expression AST
     * @return tree (with additional ROOT element)
     */
    public static OperatorNode toCNFTree(OperatorNode tree) {

        // To CNF general
        // 1. Convert to negation normal form.
        // Move NOTs inwards by repeatedly applying DeMorgan's Law. Specifically,
        // 1.1: replace ~(x | y) with (~x) & (~y); replace  with ; and
        // 1.2: replace ~(x & y) with (~x) | (~y).
        // 1.3: replace ~~x with x.

        tree = nnf(tree);

        // 2. Distribute ORs over ANDs (disjunction over conjunction):
        // A | (B & C) => (A | B) & (A | C).
        // That is: A B C & |
        //      to: A B | A C | &
        // And    : B C & A |
        //      to: B A | C A | &
        tree = distributeDoC(tree);

        return tree;
    }

    private static OperatorNode nnf(OperatorNode tree){
        tree = nnf11(tree);
        tree = nnf12(tree);
        tree = nnf13(tree);
        return tree;
    }

    private static OperatorNode nnf11(OperatorNode node){
        // Bubble down negations over OR
        // 1.1: That is: X Y | ~
        //           to: X ~ Y ~ &
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(OR)){
            // reshape tree locally
            OperatorNode or = node.left();

            return new OperatorNode(AND,
                    nnf11(new OperatorNode(NOT, or.left(), null)),
                    nnf11(new OperatorNode(NOT, or.right(), null)));
        } else {
            // search children
            return new OperatorNode(node.operator, nnf11(node.left()), nnf11(node.right())).setValue(node.value);
        }
    }

    private static OperatorNode nnf12(OperatorNode node){
        // Bubble down negations over AND
        // 1.2: That is: X Y & ~
        //           to: X ~ Y ~ |
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(AND)){
            // reshape tree locally
            OperatorNode and = node.left();

            return new OperatorNode(OR,
                    nnf12(new OperatorNode(NOT, and.left(), null)),
                    nnf12(new OperatorNode(NOT, and.right(), null)));
        } else {
            // search children
            return new OperatorNode(node.operator, nnf12(node.left()), nnf12(node.right())).setValue(node.value);
        }
    }

    private static OperatorNode nnf13(OperatorNode node){
        // Remove double negations
        // 1.2: That is: X ~ ~
        //           to: X
        if(node == null || node.isLeaf()){
            return node;
        }
        if(node.isOperator(NOT) && node.leftIsOperator(NOT)){
            // reshape tree locally
            OperatorNode not2 = node.left();
            OperatorNode x = not2.left();

            // recurse with shortened version of yourself
            return nnf13(new OperatorNode(x.operator, x.left(), x.right())).setValue(x.value);
        } else {
            // search children
            return new OperatorNode(node.operator, nnf13(node.left()), nnf13(node.right())).setValue(node.value);
        }
    }

    private static OperatorNode distributeDoC(OperatorNode node){
        // 2. Distribute ORs over ANDs (disjunction over conjunction):
        // A | (B & C) => (A | B) & (A | C).
        // That is: A B C & |
        //      to: A B | A C | &
        // And by commutativity :
        //          B C & A |
        //      to: B A | C A | &
        // note: when a,b,c are all equally likely to fail, put 'a' first
        // so all other terms don't need checking anymore.

        if(node == null || node.isLeaf()){
            return node;
        }

        // After applying this rule, an AND has drifted up in the tree.
        // its parent might as well be an OR. Therefore, do head recursion
        node = new OperatorNode(node.operator, distributeDoC(node.left()), distributeDoC(node.right())).setValue(node.value);

        if(node.isOperator(OR) && node.hasChildOperator(AND)){
            // reshape tree locally
            OperatorNode a,b,c;
            if(node.left().isOperator(AND)){
                a = node.right();
                b = node.left().left();
                c = node.left().right();
            } else {
                a = node.left();
                b = node.right().left();
                c = node.right().right();
            }

            return new OperatorNode(AND,
                    distributeDoC(new OperatorNode(OR, a, b)),
                    distributeDoC(new OperatorNode(OR, a, c)));
        } else {
            return node;
        }
    }

    public static Literal[][] toCNFArray(OperatorNode tree){
        return cnfTreeToCNFArray(toCNFTree(tree));
    }

    /**
     * Convert tree in CNF to nested array of literals.
     * @param cnfTree use toCNFTree() before calling this method
     * @return simle cnf data structure
     */
    public static Literal[][] cnfTreeToCNFArray(OperatorNode cnfTree){
        return cnfListToCNFArray(cnfTreeToCNFList(cnfTree));
    }

    public static Literal[][] cnfListToCNFArray(ArrayList<ArrayList<Literal>> cnfList){
        Literal[][] cnf = new Literal[cnfList.size()][];
        for (int i = 0; i < cnfList.size(); i++) {
            ArrayList<Literal> disjList = cnfList.get(i);
            Literal[] disjArray = new Literal[disjList.size()];
            cnf[i] = disjList.toArray(disjArray);
        }
        return cnf;
    }

    /**
     * Convert tree in CNF to nested array of literals.
     * @param cnfTree use toCNFTree() before calling this method
     * @return simle cnf data structure
     */
    public static ArrayList<ArrayList<Literal>> cnfTreeToCNFList(OperatorNode cnfTree){
        ArrayList<ArrayList<Literal>> result = new ArrayList<>();
        doConjunction(cnfTree, result);
        return result;
    }


    private static void doConjunction(OperatorNode cnfTree, ArrayList<ArrayList<Literal>> result){
        if(cnfTree.isOperator(AND)){
            doConjunction(cnfTree.left(), result);
            doConjunction(cnfTree.right(), result);
        } else {
            // must be disjunction of 1 or more literals/(negated)atoms
            ArrayList<Literal> disjunction = new ArrayList<>();
            doDisjunction(cnfTree, disjunction);
            result.add(disjunction);
        }
    }

    private static void doDisjunction(OperatorNode cnfTree, ArrayList<Literal> disjuntion){
        if(cnfTree.isOperator(OR)){
            doDisjunction(cnfTree.left(), disjuntion);
            doDisjunction(cnfTree.right(), disjuntion);
        } else {
            disjuntion.add(doLiteral(cnfTree));
        }
    }

    private static Literal doLiteral(OperatorNode node){
        boolean negated = false;
        if(node.isOperator(NOT)){
            negated = true;
            node = node.left();
        }

        if(node.isOperator("A")){
            Token[] tokens = Arrays.stream(SPACES.split(node.left().getValue())).map(w -> new Token(w).setMatchOnNormalized(true)).toArray(Token[]::new);
            return new Literal(tokens, negated, 'a');
        } else if(node.isOperator("E")){
            Token[] tokens = Arrays.stream(SPACES.split(node.left().getValue())).map(w -> new Token(w).setMatchOnNormalized(false)).toArray(Token[]::new);
            return new Literal(tokens, negated, 'a');
        } else if(node.isOperator(":")){
            return new Literal(new Token(":"+node.left().getValue()+":").setMatchOnNormalized(false), negated);
        } if(node.isOperator("1")){
            Token[] tokens = new Token[]{new Token(node.left().getValue()).setMatchOnNormalized(false)};
            return new Literal(tokens, negated, 'a');
        } else if("?".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if("+".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if("*".equals(node.getValue())){
            return new Literal(new Token(node.getValue()).setMatchOnNormalized(false), negated);
        } else if(node.isOperator("_")){
            Literal left = doLiteral(node.left());
            Literal right = doLiteral(node.right());
            // concat arrays from left and right
            Token[] tokens = Arrays.copyOf(left.tokens, left.tokens.length + right.tokens.length);
            System.arraycopy(right.tokens, 0, tokens, left.tokens.length, right.tokens.length);
            return new Literal(tokens, negated, 'a');
        } else if(node.isOperator("@")){
            return Literal.createReference(node.left().left().getValue()).setNegated(negated);
        } else if(node.isOperator("=")){
            Literal left = doLiteral(node.left());
            Literal right = doLiteral(node.right());

            // concatenate [left + op + right]
            // left
            Token[] tokens = Arrays.copyOf(left.tokens, left.tokens.length + right.tokens.length + 1);
            // op
            boolean opMatchOnNormalized = left.tokens[0].isMatchOnNormalized() && right.tokens[0].isMatchOnNormalized();
            tokens[left.tokens.length]  = new Token(node.value).setMatchOnNormalized(opMatchOnNormalized);
            // right
            System.arraycopy(right.tokens, 0, tokens, left.tokens.length + 1, right.tokens.length);

            final Literal literal = new Literal(tokens, negated, '=');
            literal.setMarker(left.tokens.length); // indicate the operator index
            return literal;
        } else {
            throw new IllegalStateException("Unexpected node in CNF expression: " + node);
        }
    }

    /**
     * Simplify expression
     * <code>
     *     'a' AND (NOT 'b' OR 'c') AND NOT ('b' AND NOT 'a') OR NOT 'b'
     *     = [[-b, a], [-b, -b, c], [-b, -b, a]]
     *     = [[-b, c], [-b, a]]
     * </code>
     *
     * @param cnf expression
     * @return simple cnf data structure
     */
    public static ArrayList<ArrayList<Literal>> simplify(ArrayList<ArrayList<Literal>> cnf){
        // Ignore empty disjunctions at this point
        cnf.removeIf(ArrayList::isEmpty);

        // Handle negated TRUE/FALSE
        cnf.forEach(disj -> disj.forEach(lit -> {
            Token token = lit.getTokens()[0];
            String word = token.getWord();
            if(lit.negated && word.equals("TRUE") && token.isMatchOnNormalized()){
                lit.negated = false;
                token.setWord("FALSE");
            } else if(lit.negated && word.equals("FALSE") && token.isMatchOnNormalized()){
                lit.negated = false;
                token.setWord("TRUE");
            }
        }));

        // Remove FALSE from disjuntions: A | FALSE -> A
        cnf.forEach(disj -> disj.removeIf(lit -> {
            Token token = lit.getTokens()[0];
            String word = token.getWord();
            return word.equals("FALSE") && token.isMatchOnNormalized();
        }));

        if(cnf.stream().anyMatch(ArrayList::isEmpty)){
            // There was a disjunction containing only FALSE, and that disj is now empty.
            // Therefore the entire CNF is false
            cnf.clear();
            ArrayList<Literal> disj = new ArrayList<>();
            disj.add(Literal.createInexact("FALSE"));
            cnf.add(disj);
            return cnf;
        }

        // Replace disjunctions containing TRUE by [TRUE]
        cnf = cnf.stream().map(disj -> {
            if(disj.stream().anyMatch(lit -> {
                        Token token = lit.getTokens()[0];
                        String word = token.getWord();
                        return word.equals("TRUE") && token.isMatchOnNormalized();}))
            {
                // Replace disj by TRUE
                ArrayList<Literal> singleTrue = new ArrayList<>();
                singleTrue.add(Literal.createInexact("TRUE"));
                return singleTrue;
            } else {
                // Keep as is
                return disj;
            }
        }).collect(Collectors.toCollection(ArrayList::new));


        // Remove duplicates in disjunction and sort the terms
        // a | a = a
        cnf = cnf.stream()
                .map(disj -> new ArrayList<>(new TreeSet<>(disj)))
                .filter(literals -> !literals.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<ArrayList<Literal>> tmp = new ArrayList<>();
        // Remove a | -a  and empty disjunctions
        nextDisj: for (ArrayList<Literal> disj : cnf) {
            ArrayList<Literal> newDisj = new ArrayList<>();
            int d = disj.size();
            for (int i = 0; i < d; i++) {
                Literal litA = disj.get(i);
                if(i + 1 < d && Arrays.equals(litA.tokens, disj.get(i + 1).tokens)) {
                    // next operand must be negated version, so:
                    // 1. disjunction is true
                    // 2. don't include the entire disjunction
                    continue nextDisj;
                }
                newDisj.add(litA);
            }

            if(!newDisj.isEmpty()){
                tmp.add(newDisj);
            }
        }
        cnf = tmp;


        // Deduplicate and sort the expression as optimization and normalization step:
        // prefer shorter disjunctions to fail fast on total conjunction
        TreeSet<ArrayList<Literal>> cnfSet = new TreeSet<>((o1, o2) -> {
            int compare = Integer.compare(o1.size(), o2.size());
            if (compare != 0) {
                return compare; // smaller ones first ...
            }
            // or order lexicographically
            for (int i = 0; i < o1.size(); i++) {
                compare = o1.get(i).compareTo(o2.get(i));
                if (compare != 0) {
                    return compare;
                }
            }
            return 0;
        });
        cnfSet.addAll(cnf);
        cnf = new ArrayList<>(cnfSet);

        // Remove disjunctions that are a prefix of another.
        // e.g. (a | b) & (a | b | c) -> (a | b)
        // use that disjunctions are sorted by the previous step

        // remove duplicate or superset disjunctions
        // a & (a | b)

        //   b | 1 0
        // ----+----
        // a 1 | 1 1
        //   0 | 0 0

        // also:
        // a & (a | b)
        // = (a & a) | (a & b)
        // = a | (a & b)

        //   b | 1 0
        // ----+----
        // a 1 | 1 1
        //   0 | 0 0
        // therefore -> a & (a | b) = a
        for (int i = 0; i < cnf.size()-1; i++) {
            ArrayList<Literal> di = cnf.get(i);
            for (int j = i+1; j < cnf.size(); j++) {
                ArrayList<Literal> dj = cnf.get(j);
                if(isNonEmptyPrefix(di, dj))
                    cnf.remove(j);
            }
        }

        // Possible other optimizations:
        // (a | b) & -b -> a & -b


        return cnf;
    }

    public static boolean isNonEmptyPrefix(List as, List bs){
        int A = as.size();
        int B = bs.size();
        if(A == 0 || A > B){
            return false;
        }
        for (int i = 0; i < as.size(); i++) {
            if(!as.get(i).equals(bs.get(i)))
                return false;
        }
        return true;
    }

    public static String toString(Literal[][] cnf){
        if(cnf == null || cnf.length == 0){
            return "()";
        }
        return Arrays.stream(cnf)
                .map(disj ->
                    Arrays.stream(disj).map(Literal::toString).collect(
                            Collectors.joining(" | ", disj.length == 1 ? "" : "(", disj.length == 1 ? "" : ")")
                    )
                )
                .collect(Collectors.joining(" & "));
    }


}
