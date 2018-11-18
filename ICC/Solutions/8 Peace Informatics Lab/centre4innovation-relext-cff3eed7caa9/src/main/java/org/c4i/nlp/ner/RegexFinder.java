package org.c4i.nlp.ner;

import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.tokenize.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find regular expression patterns
 * @author Arvid Halma
 * @version 18-10-2017 - 15:02
 */
public class RegexFinder implements EntityPlugin {

    private Pattern pattern;
    private String patternName;


    public RegexFinder(Pattern pattern, String patternName) {
        this.pattern = pattern;
        this.patternName = patternName;
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().equals(patternName);
    }

    @Override
    public List<Range> find(Token[] tokens, Literal lit, String label, int location) {

        List<Range> result = new ArrayList<>();
        Token token = tokens[location];
        Matcher matcher = pattern.matcher(token.getWord());
        if(matcher.find()) {
            String patternNameLower = patternName.toLowerCase();
            Range range = new Range(label, location, location+1, token.getCharStart(), token.getCharEnd());
            range.props.put("type", patternNameLower);
            range.props.put(patternNameLower, matcher.group(0));
            result.add(range);
        }
        return result;
    }

    @Override
    public String description() {
        return patternName;
    }

    @Override
    public String toString() {
        return "RegexFinder{" +
                "patternName='" + patternName + '\'' +
                '}';
    }
}
