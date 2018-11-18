package org.c4i.chitchat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.c4i.nlp.translate.TranslateText;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Wouter Eekhout on 14/07/2017.
 *
 */
public class TranslateTest {
    @Test
    public void translateTest() {
        String testString= "عبّر وزير" +
                " المالية والتخطيط الفلسطيني شكري بشارة عن ارتياحه لقرار المحكمة الفيدرالية في واشنطن بإسقاط" +
                " دعوى قضائية قائمة ضد السلطة الفلسطينية ومنظمة التحرير الفلسطينية للحصول على تعويضات من" +
                " قبل عائلات قتلى عملية وقعت في مستوطنة \"كرني شمرون\" (شمال الضفة الغربية المحتلة) عام 2002.";

        String expectation = "Palestinian Minister of Finance and Planning Shukri Bishara expressed his satisfaction with the decision of the Federal Court in Washington to drop a lawsuit against the Palestinian Authority and the Palestine Liberation Organization (PLO) for compensation by the families of those killed in an operation in the settlement of Karnei Shomron in the northern West Bank.";

        String result = TranslateText.arabicToEnglish(testString, new ObjectMapper());

        //Assert.assertEquals("Unexpected output", expectation, result);
    }

}
