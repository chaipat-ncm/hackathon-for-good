package org.c4i.nlp.ner;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wouter Eekhout on 13-07-17.
 *
 */
public class ArabicNamedEntityRecognition {
    //See also: https://github.com/arabicNLP/NER

    private POSTaggerME tagger;

    private final Logger logger = LoggerFactory.getLogger(ArabicNamedEntityRecognition.class);

    public ArabicNamedEntityRecognition(String dataFolder) {
        //String[] neTypes = new String[]{"PER", "LOC", "OTHER", "CLAN", "ORG", "PROPH", "AWARD"};
        try {
            InputStream modelIn = new FileInputStream(dataFolder + "ar-POS-NER.bin");
            POSModel model = new POSModel(modelIn);
            tagger = new POSTaggerME(model);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    public Map<String, String> find(String sentence) {
        //TODO This is ugly code. The provided bin file is in the form of PoSTaggerMe instead of NameFinderME. So this code transforms it.
        sentence = this.cleanLine(sentence);
        String[] tokens = this.tokenize(sentence);
        String[] tags = this.tagger.tag(tokens);
        StringBuilder segments = new StringBuilder();

        for(int i = 0; i < tags.length; ++i) {
            segments.append(tokens[i]).append("_").append(tags[i]).append(" ");
        }

        segments = new StringBuilder(segments.toString().trim());
        if(!segments.toString().endsWith("PX")) {
            segments.append(" ._PX");
        }

        tokens = segments.toString().split(" ");
        segments = new StringBuilder();
        String[] parts = tokens;
        int len$ = tokens.length;

        String BTAG = "B-";
        for(int i$ = 0; i$ < len$; ++i$) {
            String token = parts[i$];
            if(!token.contains(BTAG)) {
                segments.append(token).append(" ");
            } else {
                String[] tokenParts = token.split("_");
                String[] arr$ = this.split(tokenParts[0], tokenParts[1]);
                //int arrLen$ = arr$.length;

                for (String tokenPart : arr$) {
                    segments.append(tokenPart).append(" ");
                }
            }
        }

        Map<String, String> result = new HashMap<>();

        tokens = segments.toString().split(" ");
        String entityType;
        for(int i = 0; i < tokens.length; ++i) {
            if(tokens[i].contains(BTAG)) {
                parts = tokens[i].split(BTAG);
                entityType = parts[parts.length - 1];
                boolean ENDREACHED = false;

                for(int j = i + 1; j < tokens.length && !ENDREACHED; ++j) {
                    String ITAG = "I-";
                    if(!tokens[j].contains(ITAG) || j == tokens.length - 1) {
                        ENDREACHED = true;
                        result.put(cleanTokens(getTokens(tokens, i, j)), entityType);
                    }
                }
            }
        }

        return result;
    }

    private String cleanTokens(String tokens) {
        String[] charsToRemove = new String[]{"I-", "B-", "PER", "LOC", "OTHER", "CLAN", "ORG", "PROPH", "AWARD", "_"};
        String[] replaceBy = new String[]{"", "", "", "", "", "", "", "", "", ""};
        return StringUtils.replaceEach(tokens, charsToRemove, replaceBy);
    }

    private String getTokens(String[] tokens, int start, int end) {
        String[] subArray = Arrays.copyOfRange(tokens, start, end);
        return StringUtils.join(subArray, " ");
        //return result;
    }

    private String cleanLine(String line) {
        String[] shortVowels = new String[]{"ّ", "َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ"};
        //int len$ = shortVowels.length;

        for (String shortVowel : shortVowels) {
            line = line.replaceAll(shortVowel, "");
        }

        line = line.replaceAll("(؛|-|%|\\(|\\)|/|\\:|\\.|\\?|\\؟|\\;|\\,|\\!|\\،|\\\"|–)", " $0 ");

        do {
            line = line.replaceAll("  ", " ");
        } while(line.contains("  "));

        line = line.replaceAll("»", "» ");
        line = line.replaceAll("«", "« ");
        return line;
    }

    private String[] tokenize(String sentence) {
        return sentence.trim().split(" ");
    }

    private String[] split(String token, String pos) {
        String[] parts;
        if(!pos.contains("+")) {
            parts = new String[]{token + "_" + pos};
            return parts;
        } else {
            int k = 0;
            String[] subTags = pos.split("\\+");
            parts = new String[subTags.length];

            //String prevTag = "";
            String rest = token;
            String prevToken;
            for(int i = 0; i < subTags.length - 1; ++i) {
                String prevTag = subTags[i];
                String p = "P";
                String l = "ل";
                if(prevTag.equalsIgnoreCase(p) && rest.startsWith(l + l)) {
                    prevToken = rest.substring(0, 1);
                    String a = "ا";
                    rest = a + rest.substring(1);
                } else {
                    prevToken = rest.substring(0, 1);
                    rest = rest.substring(1);
                }

                parts[k++] = prevToken + "_" + prevTag;
            }

            parts[k] = rest + "_" + subTags[subTags.length - 1];
            return parts;
        }
    }
}
