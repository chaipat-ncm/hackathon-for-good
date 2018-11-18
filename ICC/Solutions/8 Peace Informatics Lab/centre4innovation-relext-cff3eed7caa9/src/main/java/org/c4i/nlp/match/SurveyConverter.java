package org.c4i.nlp.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.c4i.nlp.tokenize.MatchingWordTokenizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.util.Csv;
import org.parboiled.common.ImmutableList;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Convert a tree of questions and answers to a chitchat {@link Script}
 * @author Arvid Halma
 */
public class SurveyConverter {

    private static final ObjectMapper OBJECT_MAPPER;
    static {
        OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static Survey parseYaml(String yaml) throws IOException {
        return OBJECT_MAPPER.readValue(yaml, Survey.class);
    }


    public static String convertYaml(String yaml) throws IOException {
        return convertWithPlainText(parseYaml(yaml));
    }

    public static Survey parseCsv(String csv) throws IOException {
        Survey survey = new Survey();
        survey.questions = new LinkedHashMap<>();

        Question q = new Question();

        final List<Csv.Row> rows = new Csv().setInput(csv).getRows();
        for (Csv.Row row : rows) {
            String qId = row.getString(0);
            String qText = row.getString(1);
            String aId = row.getString(2);
            String aText = row.getString(3);
            String gotoQId = row.getString(4);

            if(aId == null || aText == null){
                continue; // ignore empty lines
            }

            if(qId != null){
                q = new Question();
                q.text = qText;
                q.answers = new LinkedHashMap<>();
                survey.questions.put(qId, q);
            }

            final Answer a = new Answer();
            a.text = aText;
            a.kontinue = gotoQId;
            q.answers.put(aId, a);
        }

        return survey;
    }

    public static String convertCsv(String csv, String style) throws IOException {
        Survey survey = parseCsv(csv);
        return "buttons".equalsIgnoreCase(style) ? convertWithButtons(survey) : convertWithPlainText(survey);
    }

    public static String convertCsv(String csv) throws IOException {
        return convertWithPlainText(parseCsv(csv));
    }

    public static String convertWithPlainText(Survey survey){

        StringBuilder src = new StringBuilder();

        boolean first = true;
        MatchingWordTokenizer tokenizer = new MatchingWordTokenizer();
        for (Map.Entry<String, Question> questionEntry : survey.questions.entrySet()) {
            String qId = questionEntry.getKey();
            String aqId = questionEntry.getKey() + "answer";
            Question q = questionEntry.getValue();

            if(first){
                src.append('@').append(qId).append(" <- *\n");
                first = false;
            }

            // Ask question and list the options

            src.append('@').append(qId).append(" {addLabel: ").append(aqId).append(", within: 1} -> ");
            src.append(q.text).append(" & ");
            src.append(String.join(" & ", q.answers.values().stream().map(a -> a.text).collect(Collectors.toList())));
            src.append('\n');

            // Make list of common words among different answers. don't match on those.
            List<List<Token>> allAnswerTokens = new ArrayList<>();
            for (Answer a : q.answers.values()) {
                allAnswerTokens.add(tokenizer.tokenize(a.text));
            }
            Set<Token> ambiTokens = commonElements(allAnswerTokens);


            // Add labels
            for (Map.Entry<String, Answer> answerEntry : q.answers.entrySet()) {
                String aId = answerEntry.getKey();
                Answer a = answerEntry.getValue();
                String ansId = qId + aId;

                // Add answer label for when this option matches
                src.append('@').append(ansId).append(" <- @").append(aqId).append(" & (");
                src.append(String.join(" | ", tokenizer.tokenize(a.text).stream().filter(t -> !ambiTokens.contains(t)).map(Token::getWord).collect(Collectors.toList())));
                src.append(")\n");

                // Add label to trigger new question
                if(a.kontinue != null){
                    src.append('@').append(a.kontinue).append(" <- ").append('@').append(ansId).append('\n');
                }
            }

            String invalidCondition = '@' + aqId + " & ((" +
                    allPairs(new ArrayList<>(q.answers.keySet())).stream().map(combi -> '@' + qId + combi.get(0) + " & @" + qId + combi.get(1)).collect(Collectors.joining(" | ")) + ") | -("+ // ambiguous
                    q.answers.keySet().stream().map(a -> '@' + qId + a).collect(Collectors.joining(" | ")) + "))"; // none answered
            final String invalidQ = qId + "invalid";
            src.append('@').append(invalidQ).append(" <- ") .append(invalidCondition).append('\n');
            src.append('@').append(invalidQ).append(" {repeat, addLabel: ").append(aqId).append("} -> Please choose one of the suggested answers...\n");
            src.append('\n');
        }
        return src.toString();
    }


    public static String convertWithButtons(Survey survey){

        StringBuilder src = new StringBuilder();

        boolean first = true;
        MatchingWordTokenizer tokenizer = new MatchingWordTokenizer();
        for (Map.Entry<String, Question> questionEntry : survey.questions.entrySet()) {
            String qId = questionEntry.getKey();
            String aqId = questionEntry.getKey() + "answer";
            Question q = questionEntry.getValue();

            if(first){
                src.append('@').append(qId).append(" <- *\n");
                first = false;
            }

            // Ask question and list the options

            src.append('@').append(qId).append(" {addLabel: ").append(aqId).append(", within: 1} -> ");
            src.append(q.text).append(" & ");
            src.append(String.join(" ", q.answers.entrySet().stream().map(aEntry -> "BUTTON("+aEntry.getValue().text+", "+aEntry.getKey()+")").collect(Collectors.toList())));
            src.append('\n');



            // Add labels
            for (Map.Entry<String, Answer> answerEntry : q.answers.entrySet()) {
                String aId = answerEntry.getKey();
                Answer a = answerEntry.getValue();
                String ansId = qId + aId;

                // Add answer label for when this option matches
                src.append('@').append(ansId).append(" <- @").append(aqId).append(" & ").append(aId).append("\n");

                // Add label to trigger new question
                if(a.kontinue != null){
                    src.append('@').append(a.kontinue).append(" <- ").append('@').append(ansId).append('\n');
                }
            }

            src.append('\n');
        }
        return src.toString();
    }

    public String toYaml(Survey survey) throws IOException {
        StringWriter writer = new StringWriter();
        OBJECT_MAPPER.writeValue(writer, survey);
        return writer.toString();
    }

    public static <A> List<List<A>> allPairs(List<A> xs){
        List<List<A>> result = new ArrayList<>();
        int n = xs.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++) {
                A xi = xs.get(i);
                A xj = xs.get(j);
                result.add(ImmutableList.of(xi, xj));
            }
        }
        return result;
    }


    public static <A> Set<A> commonElements(Collection<? extends Collection<A>> collections){
        List<Set<A>> sets = collections.stream().map(HashSet::new).collect(Collectors.toList());

        Set<A> result = new HashSet<>();
        int S = sets.size();
        for (int i = 0; i < S-1; i++) {
            for (int j = i+1; j < S; j++) {
                Set<A> seti = sets.get(i);
                Set<A> setj = sets.get(j);

                for (A a : setj) {
                    if(seti.contains(a)){
                        result.add(a);
                    }
                }
            }
        }
        return result;
    }

}
