package org.c4i.nlp;

import org.c4i.util.Csv;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Arvid Halma
 * @version 4-12-17
 */
public class ConvertThesArDemo {

    public static void main(String[] args) throws IOException {
        Pattern swds = Pattern.compile("swds\":\"([^\"]+)\"");

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/nlp/en/en-synonyms.csv"), StandardCharsets.UTF_8))) {


            new Csv().formatTsv().setInputFile("/data/thesaurus_ar/thesaurus_ar2.csv").process(row -> {
                String word = row.excelGetString("A");
//            System.out.println("word = " + word);
                Matcher matcher = swds.matcher(row.excelGetString("B"));
                if (matcher.find()) {
                    String syns = matcher.group(1);
//                System.out.println("syns = " + syns);
                    String[] syn = syns.split(", ");

                    try {
                        bw.write(word + "\t" + String.join("\t", syn));
                        bw.write('\n');
                    } catch (IOException e) {

                    }
                }
            });

        }
    }
}
