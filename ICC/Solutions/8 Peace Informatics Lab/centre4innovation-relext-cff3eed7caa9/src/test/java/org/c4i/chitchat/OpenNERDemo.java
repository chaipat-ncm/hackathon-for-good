package org.c4i.chitchat;

import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.ner.OpenNER;
import org.c4i.nlp.tokenize.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * NER tests
 * @author Arvid Halma
 */
public class OpenNERDemo {

    public static void main(String[] args) throws IOException {

        /*String text = "Pierre Vinken , 61 years old , will join the board as a nonexecutive director Nov. 29 . " +
                      "Mr . Vinken is chairman of Elsevier N.V. , the Dutch publishing group . " +
                      "Rudolph Agnew , 55 years old and former chairman of Consolidated Gold Fields PLC , was named a director of this British industrial conglomerate ." +
                "Vitamine C is great . But not as great as vitamine a . An then we have the new iPhone ! ";*/
        String text = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. " +
                      "Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. " +
                      "Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate." +
                "Vitamine C is great. But not as great as vitamine a. An then we have the new iPhone! ";

        Tokenizer tokenizer = new MatchingWordTokenizer();
//        Tokenizer tokenizer = new OpenTokenizer(new File("data/nlp/en/en-token.bin"));

        List<Token> tokens = tokenizer.tokenize(text);
        System.out.println("text = " + text);

        System.out.println(" ===== EN ====== ");
        OpenNER personFinder = new OpenNER(new File("data/nlp/en/en-ner-person.bin"));
        List<Range> personRanges = personFinder.find(tokens);
        personRanges.forEach(mr -> mr.updateValue(text));
        System.out.println("PERSON...");
        System.out.println(Eval.highlightWithTags(text, personRanges));
        for (Range personRange : personRanges) {
            System.out.println("person = " + personRange.value);
        }
        System.out.println();

        OpenNER orgFinder = new OpenNER(new File("data/nlp/en/en-ner-organization.bin"));
        List<Range> orgRanges = orgFinder.find(tokens);
        orgRanges.forEach(mr -> mr.updateValue(text));
        System.out.println("ORGANIZATION...");
        System.out.println(Eval.highlightWithTags(text, orgRanges));
        for (Range orgRange : orgRanges) {
            System.out.println("org = " + orgRange.value);
        }
        System.out.println();

        OpenNER dateFinder = new OpenNER(new File("data/nlp/en/en-ner-date.bin"));
        List<Range> dateRanges = dateFinder.find(tokens);
        dateRanges.forEach(mr -> mr.updateValue(text));
        System.out.println("DATE...");
        System.out.println(Eval.highlightWithTags(text, dateRanges));
        for (Range dateRange : dateRanges) {
            System.out.println("date = " + dateRange.value);
        }
        System.out.println();

    }
}
