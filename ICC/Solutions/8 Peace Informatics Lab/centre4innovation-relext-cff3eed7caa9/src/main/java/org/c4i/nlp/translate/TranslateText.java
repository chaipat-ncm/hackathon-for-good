package org.c4i.nlp.translate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Wouter Eekhout on 14/07/2017.
 *
 */
//TODO This class is not meant for a production environment. It is only meant for debugging. Replace this class for a more reliable solution!
public class TranslateText {
    private static final Logger logger = LoggerFactory.getLogger(TranslateText.class);

    /**
     * !!!! This class is not meant for a production environment. It is only meant for debugging.
     * !!!! Replace this class for a more reliable solution!
     * It translate arabic text to english
     * @param arabicText The input text
     * @param objectMapper json object reader
     * @return The translate text
     */
    public static String arabicToEnglish(String arabicText, ObjectMapper objectMapper) {
        return callUrlAndParseResult("ar", "en", arabicText, objectMapper);
    }

    /**
     * !!!! This class is not meant for a production environment. It is only meant for debugging.
     * !!!! Replace this class for a more reliable solution!
     * It translate the input text to a different language
     * @param langFrom The language of the input text
     * @param langTo The language the text needs to be translate to.
     * @param text The input text
     * @param objectMapper json object reader
     * @return The translate text
     */
    public static String translate(String langFrom, String langTo, String text, ObjectMapper objectMapper) {
        return callUrlAndParseResult(langFrom, langTo, text, objectMapper);
    }


    private static String callUrlAndParseResult(String langFrom, String langTo, String text, ObjectMapper objectMapper)
    {
        try {
            String url = "https://translate.googleapis.com/translate_a/single?" +
                    "client=gtx&" +
                    "sl=" + langFrom +
                    "&tl=" + langTo +
                    "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            response.append("{ \"data\": ");
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            response.append("}");
            in.close();

            return parseResult(response.toString(), objectMapper);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return "";
    }

    private static String parseResult(String inputJson, ObjectMapper objectMapper) throws Exception
    {
      /*
       * inputJson for word 'hello' translated to language Hindi from English-
       * [[["नमस्ते","hello",,,1]],,"en"]
       * We have to get 'नमस्ते ' from this json.
       */
        JsonNode rootNode = objectMapper.readTree(inputJson);
        return rootNode.path("data").get(0).get(0).get(0).asText();
    }
}
