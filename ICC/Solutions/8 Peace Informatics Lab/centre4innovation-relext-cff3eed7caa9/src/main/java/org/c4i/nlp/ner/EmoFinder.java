package org.c4i.nlp.ner;

import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.tokenize.Token;
import org.c4i.util.EmojiAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Find integer numbers
 * @author Arvid Halma
 * @version 18-10-2017 - 15:02
 */
public class EmoFinder implements EntityPlugin {

    public EmoFinder() {
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().startsWith("EMO");
    }

    @Override
    public List<Range> find(Token[] tokens, Literal lit, String label, int location) {

        Token token = tokens[location];
        List<Range> result = new ArrayList<>(1);
        Range range = new Range(label, location, location+1, token.getCharStart(), token.getCharEnd());
        result.add(range);

        String match = EmojiAlias.wordToMatchCode(tokens[location].getWord());
        if(match != null){
            range.props.put("type", "emoticon");
            range.props.put("emoticon", match);
            return result;
        }

        return new ArrayList<>(0);
    }

    @Override
    public String description() {
        return "EMO";
    }

    @Override
    public String toString() {
        return "EmoFinder{}";
    }
}
