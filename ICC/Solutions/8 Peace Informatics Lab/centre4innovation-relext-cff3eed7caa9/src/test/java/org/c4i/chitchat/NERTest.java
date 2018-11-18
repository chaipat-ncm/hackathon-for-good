package org.c4i.chitchat;

import org.c4i.nlp.ner.ArabicNamedEntityRecognition;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wouter Eekhout on 13-07-17.
 *
 */
public class NERTest {

    @Test
    public void NERTest() {
        ArabicNamedEntityRecognition ner = new ArabicNamedEntityRecognition("data/nlp/ar/");
        String testString= "عبّر وزير" +
                " المالية والتخطيط الفلسطيني شكري بشارة عن ارتياحه لقرار المحكمة الفيدرالية في واشنطن بإسقاط" +
                " دعوى قضائية قائمة ضد السلطة الفلسطينية ومنظمة التحرير الفلسطينية للحصول على تعويضات من" +
                " قبل عائلات قتلى عملية وقعت في مستوطنة \"كرني شمرون\" (شمال الضفة الغربية المحتلة) عام 2002.";
        //Span[] spans = ner.find(testString);

        Map<String, String> result = ner.find(testString);


        Map<String, String> expectation = new HashMap<>();
        expectation.put("الضفة الغربية", "LOC");
        expectation.put("السلطة الفلسطينية", "ORG");
        expectation.put("المحكمة الفيدرالية", "ORG");
        expectation.put("كرني شمرون", "LOC");
        expectation.put("شكري بشارة", "PER");
        expectation.put("واشنطن", "LOC");
        expectation.put("منظمة التحرير الفلسطينية", "ORG");

        Assert.assertEquals("Unexpected amount of tags returned", expectation.size(), result.size());

        for (String key: expectation.keySet()) {
            if(!result.containsKey(key)) {
                Assert.fail("The result did not has the expected key");
            }

            Assert.assertEquals("The entities do not match", expectation.get(key), result.get(key));
        }
    }
}
