package org.c4i.nlp.match;

import org.c4i.nlp.tokenize.Token;
import org.parboiled.common.ImmutableList;

import java.util.List;

/**
 * TRUE and FALSE literals.
 * @author Arvid Halma
 */
public class TrueFalse implements EntityPlugin {
    @Override
    public boolean accept(Literal lit) {
        String word = lit.getTokens()[0].getWord();
        return word.equals("TRUE") || word.equals("FALSE");
    }

    @Override
    public List<Range> find(Token[] text, Literal lit, String label, int location) {
        // run only once, at sentence level.
        if(location > 0){
            return ImmutableList.of();
        }
        String word = lit.getTokens()[0].getWord();
        if(word.equals("TRUE")){
            int n = text.length;
            return ImmutableList.of(new Range("TRUE", 0, n, 0, text[n-1].getCharEnd()));
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public String description() {
        return "BOOLEAN";
    }
}
