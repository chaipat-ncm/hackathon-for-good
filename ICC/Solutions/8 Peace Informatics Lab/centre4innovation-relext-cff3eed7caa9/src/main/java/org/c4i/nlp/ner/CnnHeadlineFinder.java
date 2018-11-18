package org.c4i.nlp.ner;

import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.tokenize.Token;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve a live headline from CNN.
 * @author Arvid Halma
 * @version 18-10-2017 - 15:02
 */
public class CnnHeadlineFinder implements EntityPlugin {


    public CnnHeadlineFinder() {
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().equals("CNNHEADLINE");
    }

    @Override
    public List<Range> find(Token[] tokens, Literal lit, String label, int location) {
        List<Range> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://lite.cnn.io/en").get();
            String headline = doc.select("li").first().text();
            Range number = new Range(label, 0, 1, 0, 1);
            number.props.put("type", "cnnheadline");
            number.props.put("headline", headline);
            result.add(number);
        } catch (Exception ignored) {}

        return result;
    }

    @Override
    public String description() {
        return "CNNHEADLINE";
    }

    @Override
    public String toString() {
        return "CnnHeadlineFinder{}";
    }
}
