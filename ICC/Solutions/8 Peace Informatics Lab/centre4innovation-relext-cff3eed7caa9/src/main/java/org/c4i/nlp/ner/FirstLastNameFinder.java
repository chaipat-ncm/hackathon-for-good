package org.c4i.nlp.ner;

import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.tokenize.Token;
import org.parboiled.common.ImmutableList;

import java.util.List;
import java.util.regex.Pattern;

/**
 * First LAst name literals.
 * @author Arvid Halma
 */
public class FirstLastNameFinder implements EntityPlugin {

    static Pattern FIRST = Pattern.compile("(([A-Z]\\.){1,3})|([A-Z][a-z]{3,})");
    static Pattern LAST = Pattern.compile("([A-Z][a-zA-Z]{3,})");

    @Override
    public boolean accept(Literal lit) {
        String word = lit.getTokens()[0].getWord();
        return word.equals("FIRSTLASTNAME");
    }

    @Override
    public List<Range> find(Token[] text, Literal lit, String label, int location) {
        if(location > 0 && location < text.length - 1){
            String first = text[location].getWord();
            String last = text[location+1].getWord();

            if(FIRST.matcher(first).find() && LAST.matcher(last).find()){
                return ImmutableList.of(new Range(label, location, location + 2, text[location].getCharStart(), text[location+1].getCharEnd()));
            }

        }
        return ImmutableList.of();
    }

    @Override
    public String description() {
        return "BOOLEAN";
    }
}
