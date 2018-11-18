package org.c4i.nlp;

import org.c4i.nlp.match.MatchUtil;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.*;

import java.util.Arrays;

public class TokenizerDemo {
    static Tokenizer tokenizer = new MatchingWordTokenizer();

    public static void main(String[] args) {
        show1("");
        show1(".");
        show1(". .");
        show1("hi");
        show1("hello world.");
        show1("hello world .");
        show1("hello world . ");
        show1("hello world ... ");
        show1("hello world .. . ");
        show1("hello! . 1 aap .2 noot. 3 mies .");
        show1("\t\n" +
                "How much does it rain tomorrow?");

        testSections();

    }

    private static void testSections() {
        System.out.println("\n\nTest paragraphs 1");
        testSections("aa aa a\n\nbbbb b. b b\n\n.ccc ccc cc");
        testSectionsFlat("aa aa a\n\nbbbb b. b b\n\nccc ccc cc");
        System.out.println("\n\nTest paragraphs 2");
        testSections("\n\naa aa a\n  \n bbbb b. b b\n\n.ccc ccc cc");
        testSectionsFlat("\n\naa aa a\n  \n bbbb b. b b\n\nccc ccc cc");
    }
    private static void testSections(String text) {
        final Token[][][] tokens = MatchUtil.textToSectionTokens(text,StringNormalizers.DEFAULT, tokenizer, new RegexSentenceSplitter());
        for (int i = 0; i < tokens.length; i++) {
            System.out.println("PART = " + i);
            final String s = TokenUtil.toSentence(TokenUtil.concatAll(tokens[i]));
            System.out.println("s = " + s);

        }
    }

    private static void testSectionsFlat(String text) {
        final Token[][] tokens = MatchUtil.textToSentenceTokensWithSections(text,StringNormalizers.DEFAULT, tokenizer, new RegexSentenceSplitter());
        for (Token[] token : tokens) {
            final String s = TokenUtil.toSentence(token);
            System.out.println(token[0].getSection() + " =  " + s);

        }
    }

    public static void show1(String txt){
        Token[][] tokens = MatchUtil.textToSentenceTokens(txt, StringNormalizers.DEFAULT, tokenizer, new RegexSentenceSplitter());
        System.out.println("text   = " + txt);
        System.out.println("tokens = " + Arrays.deepToString(tokens));
        System.out.println();

    }
}
