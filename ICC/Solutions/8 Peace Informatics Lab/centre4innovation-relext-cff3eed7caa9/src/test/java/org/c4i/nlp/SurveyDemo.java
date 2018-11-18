package org.c4i.nlp;

import com.google.common.collect.ImmutableList;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Survey;
import org.c4i.nlp.match.SurveyConverter;

import java.io.IOException;
import java.util.Set;

public class SurveyDemo {


    public static void ambis(){
        Set<String> strings = SurveyConverter.commonElements(ImmutableList.of(
                ImmutableList.of("a", "b", "c", "d"),
                ImmutableList.of("g", "h", "c"),
                ImmutableList.of("d", "z")
        ));

        System.out.println("common = " + strings);

        System.out.println("pairs = " + SurveyConverter.allPairs(ImmutableList.of("a", "b", "c", "d")));
    }

    public static void main(String[] args) throws IOException {
        ambis();

        String src =
                "questions: \n" +
                        "  q1: \n" +
                        "    text: \"How are you? Please choose:\"\n" +
                        "    answers:\n" +
                        "      good: {text: \"1. Gooood!!\"}\n" +
                        "      bad: {text: \"2. Bad...\", continue: \"q2\"}\n" +
                        "  q2:\n" +
                        "    text: \"Why are you feeling bad? Please choose:\"\n" +
                        "    answers: \n" +
                        "      nocoffee: {text: \"1. I didn't have coffee\"}\n" +
                        "      unknown: {text: \"2. I don't know\"}\n";

        Survey survey = SurveyConverter.parseYaml(src);

        String script = SurveyConverter.convertWithPlainText(survey);
        System.out.println("script = " + script);

        Compiler.compile(script).validate();
    }
}
