package org.c4i.nlp.tokenize;

import java.util.regex.Pattern;

/**
 * Heuristically splits on relevant periods, question marks and exclamation marks.
 * Source: https://regex101.com/r/nG1gU7/27
 * Example.
 * <pre>
 *     Mr. Smith bought cheapsite.com for 1.5 million dollars, i.e. he paid a lot for it. Did he mind? Adam Jones Jr. thinks he didn't. In any case, this isn't true... Well, with a probability of .9 it isn't.
 * </pre>
 * becomes
 * <pre>
     - Mr. Smith bought cheapsite.com for 1.5 million dollars, i.e. he paid a lot for it.
     - Did he mind?
     - Adam Jones Jr. thinks he didn't.
     - In any case, this isn't true...
     - Well, with a probability of .9 it isn't.
 * </pre>
 * @author Arvid Halma
 * @version 9-6-2017 - 20:41
 */
public class OldRegexSentenceSplitter implements SentenceSplitter {

    public static final Pattern sentence = Pattern.compile("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=[.?!]\"?)\\s");

    @Override
    public String description() {
        return "regex splitter";
    }

    @Override
    public String[] split(String text) {
        return sentence.split(text);
    }
}
