package org.c4i.nlp.match;

import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.TokenUtil;
import org.c4i.util.ArrayUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A literal is an atomic formula (in this case a sequence of tokens) or its negation.
 * The definition mostly appears in proof theory (of classical logic), e.g. in conjunctive normal form.
 * It just happens to be used for that here as well.
 * <p>
 * This literal is just a container for a single tokens.
 * Evaluation is done elsewhere. In this way, serialization and expression transformations can be
 * done independently from the underlying semantics.
 *
 * @author Arvid Halma
 * @version 27-4-2016 - 20:51
 */
public class Literal implements Comparable<Literal>{
    Token[] tokens;
    boolean negated; // sign of the literal (A or NOT A)
    char meta;       // type annotation
    int marker;      // free to interpret indicator

    public Literal(Token[] tokens, boolean negated, char meta) {
        this.tokens = tokens;
        this.negated = negated;
        this.meta = meta;
    }

    public Literal(Token token, boolean negated, char meta) {
        this(new Token[]{token}, negated, meta);
    }

    public Literal(Token token, char meta) {
        this(new Token[]{token}, false, meta);
    }

    public Literal(Token token, boolean negated) {
        this(new Token[]{token}, negated, 'a');
    }

    public Literal(Token token) {
        this(new Token[]{token}, false, 'a');
    }

    public static Literal createReference(String label) {
        return new Literal(new Token(label).setMatchOnNormalized(false), '@');
    }

    public static Literal createExact(String text) {
        return new Literal(new Token(text).setMatchOnNormalized(false), 'a');
    }

    public static Literal createInexact(String text) {
        Token[] tokens = new MatchingWordTokenizer().tokenize(text).toArray(new Token[0]);
        for (Token token : tokens) {
            token.setMatchOnNormalized(true);
        }
        return new Literal(tokens, false, 'a');
    }


    public static Literal[][] createAnd(Literal ... lits) {
        return new Literal[][]{lits};
    }

    public static Literal[][] createOr(Literal ... lits) {
        Literal[][] result = new Literal[lits.length][];
        for (int i = 0; i < lits.length; i++) {
            result[i] = new Literal[]{lits[i]};
        }
        return result;
    }

    public Token[] getTokens() {
        return tokens;
    }

    public Literal setTokens(Token[] tokens) {
        this.tokens = tokens;
        return this;
    }

    public boolean isNegated() {
        return negated;
    }

    public Literal setNegated(boolean negated) {
        this.negated = negated;
        return this;
    }

    public char getMeta() {
        return meta;
    }

    public Literal setMeta(char meta) {
        this.meta = meta;
        return this;
    }

    public int getMarker() {
        return marker;
    }

    public Literal setMarker(int marker) {
        this.marker = marker;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;

        Literal literal = (Literal) o;

        if (negated != literal.negated) return false;
        if (meta != literal.meta) return false;
        return Arrays.equals(tokens, literal.tokens);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(tokens);
        result = 31 * result + (negated ? 1 : 0);
        result = 31 * result + (int) meta;
        return result;
    }

    @Override
    public String toString() {
        if(meta == '='){
            // comparison

            // get operands
            final Token[] leftTokens = ArrayUtil.subArray(tokens, 0, marker);
            final Token[] rightTokens = ArrayUtil.subArray(tokens, marker+1, tokens.length);

            String leftWord = TokenUtil.toSentence(leftTokens);
            String rightWord = TokenUtil.toSentence(rightTokens);

            if(leftTokens[0].isMatchOnNormalized()){
                leftWord = '\'' + leftWord + '\'';
            } else {
                leftWord = '\"' + leftWord + '\"';
            }

            if(rightTokens[0].isMatchOnNormalized()){
                rightWord = '\'' + rightWord + '\'';
            } else {
                rightWord = '\"' + rightWord + '\"';
            }


            return leftWord+ " " + tokens[marker] + " " + rightWord;
        }

        String tokens = Arrays.stream(this.tokens)
                .map(token -> {
                    String word = token.getWord();
                    return !token.isMatchOnNormalized() && meta == 'a' && !"?+*".contains(word) ? "\"" + word + "\"" : word;
                })
                .collect(Collectors.joining("_"));
        if(meta == '@'){
            tokens = meta+tokens;
        }
        if(negated){
            tokens = "-"+tokens;
        }
        return tokens;
    }

    @Override
    public int compareTo(Literal lit) {
        int metaCmp = Character.compare(meta, lit.meta);
        if(metaCmp != 0){
            return metaCmp;
        }

        for (int i = 0; i < tokens.length && i < lit.tokens.length ; i++) {
            Token token = tokens[i];

            int tokenComp = token.compareTo(lit.tokens[i]);
            if(tokenComp == 0) {
                if (negated && !lit.negated) {
                    return -1;
                } else if(!negated && lit.negated){
                    return 1;
                }
            } else {
                return tokenComp;
            }
        }
        // equals so far...
        return Integer.compare(tokens.length, lit.tokens.length);
    }
}
