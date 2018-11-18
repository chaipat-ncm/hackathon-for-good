package org.c4i.nlp.tokenize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieve list of words from a text. The text is split on whitespace, punctuation and the like.
 * @author Arvid Halma
 * @version 10-5-2015 - 20:55
 */
public class MatchingWordTokenizer implements Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(?U)(?<![\\w+\\-])" // left word boundary
                    + "([\\-+]?([0-9]+([.,][0-9]+)))" // number like
                    + "|(\\w([\\w\\-'])*\\w)|\\w" // a word, with ' or - in the middle
                              + "|(([a-z0-9_.-]+)@([\\da-z.-]+)\\.([a-z.]{2,6}))" //email
                              + "|((https?[:]//)?[\\w./#-]{4,})" //url
//                              + "|((https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .#\\-]*)*/?)" //url
                              + "|(?:\\s|\\A|^)[#]([\\w\\-_]+)" //hash tags
                              + "|(?:\\s|\\A|^)[@]([\\w\\-_]+)" //Twitter handles
                              + "|(?:\\s|\\A|^)[$]([\\w\\-_]+(\\.[\\w\\-_]+)?)" //property string interpolation
                              + "|([()\\-;:pD]{2,3})" // emoji ascii
                              // + "|([\uD83C-\uDBFF\uDC00-\uDFFF]+)" // emoji (not working)
                              + "|(?:\\s|\\A|^)[:]([\\w-_]+)[:]" //Short code :emoticons:
                    + "(?!\\w)" // right word boundary
//            , Pattern.UNICODE_CHARACTER_CLASS

    );

    public MatchingWordTokenizer() {
    }

    @Override
    public List<Token> tokenize(String text){
        Matcher matcher = TOKEN_PATTERN.matcher(text);
        List<Token> words = new ArrayList<>();
        int loc = 0;
        while (matcher.find()) {
            Token token = new Token(matcher.group().trim(), loc++);
            token.setCharStart(matcher.start()).setCharEnd(matcher.end());
            words.add(token);
        }
        return words;
    }

    public List<Token> tokenizeFull(String text){
        Matcher matcher = TOKEN_PATTERN.matcher(text);
        List<Token> words = new ArrayList<>();
        int loc = 0;
        int lastEnd = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if(start > lastEnd){
                // there was a non-matching part
                String word = text.substring(lastEnd, start).trim();
                if(!word.isEmpty()) {
                    Token token = new Token(word, loc++);
                    token.setCharStart(lastEnd).setCharEnd(start);
                    words.add(token);
                }
            }

            Token token = new Token(matcher.group().trim(), loc++);
            token.setCharStart(start).setCharEnd(end);
            words.add(token);
            lastEnd = end;
        }
        int n = text.length();
        if(n > lastEnd){
            // put tail

            String word = text.substring(lastEnd, n).trim();
            if(!word.isEmpty()) {
                Token token = new Token(word, loc);
                token.setCharStart(lastEnd).setCharEnd(n);
                words.add(token);
            }
        }

        return words;
    }

    public String description(){
        return "matching words";
    }
}
