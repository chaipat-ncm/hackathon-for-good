package org.c4i.chitchat;

import org.c4i.nlp.pos.ArabicPoS;
import org.c4i.nlp.pos.OpenPOSTagger;
import org.c4i.nlp.pos.POSTagger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wouter Eekhout on 13-07-17.
 *
 */
public class PoSTest {
    @Test
    public void poSTest() {
        String input = "وفي عام 1994، في مسلسل \"ذا فريش برينس أوف بيل أير\"، لم يكد يراه كارلتون، المؤيد للحزب الجمهوري المحافظ، يدخل من الباب حتى سقط مغشيا عليه.";
        //Expected: وفي_C+P عام_N 1994_NM ،_PX في_P مسلسل_N "_PX ذا_B-NE فريش_I-NE برينس_I-NE أوف_I-NE بيل_I-NE أير_I-NE "_PX ،_PX لم_NEG يكد_V يراه_V+PRO كارلتون_B-NE ،_PX المؤيد_D+AJ للحزب_P+B-NE الجمهوري_I-NE المحافظ_D+AJ ،_PX يدخل_V من_P الباب_D+N حتى_P سقط_V مغشيا_AJ عليه_P+PRO
        ArabicPoS pos = new ArabicPoS("data/nlp/ar/");

        String[] tags = pos.postag(input);

        List<String> expected = new ArrayList<>();
        expected.add("C+P");
        expected.add("N");
        expected.add("NM");
        expected.add("P");
        expected.add("N");
        expected.add("PX");
        expected.add("B-NE");
        expected.add("I-NE");
        expected.add("I-NE");
        expected.add("I-NE");
        expected.add("I-NE");
        expected.add("NEG");
        expected.add("V");
        expected.add("V+PRO");
        expected.add("B-NE");
        expected.add("D+AJ");
        expected.add("P+B-NE");
        expected.add("I-NE");
        expected.add("D+AJ");
        expected.add("V");
        expected.add("P");
        expected.add("D+N");
        expected.add("P");
        expected.add("V");
        expected.add("AJ");
        expected.add("P+PRO");

        Assert.assertEquals("Unexpected amount of tags returned", expected.size(), tags.length);

        for(int i = 0; i < expected.size(); ++i) {
            Assert.assertEquals("Not the expected tag", expected.get(i), tags[i]);
        }
    }

    @Test
    public void posTest2() throws IOException {
        String input = "وفي عام 1994، في مسلسل \"ذا فريش برينس أوف بيل أير\"، لم يكد يراه كارلتون، المؤيد للحزب الجمهوري المحافظ، يدخل من الباب حتى سقط مغشيا عليه.";
        //Expected: وفي_C+P عام_N 1994_NM ،_PX في_P مسلسل_N "_PX ذا_B-NE فريش_I-NE برينس_I-NE أوف_I-NE بيل_I-NE أير_I-NE "_PX ،_PX لم_NEG يكد_V يراه_V+PRO كارلتون_B-NE ،_PX المؤيد_D+AJ للحزب_P+B-NE الجمهوري_I-NE المحافظ_D+AJ ،_PX يدخل_V من_P الباب_D+N حتى_P سقط_V مغشيا_AJ عليه_P+PRO

        POSTagger pos = new OpenPOSTagger(new File("data/nlp/ar/ar-POS.bin"));

        String[] tags = pos.tagStrings(input.split(" "));

        String[] expected = "C+P N NM P N PX B-NE I-NE I-NE I-NE I-NE NEG V V+PRO B-NE D+AJ P+B-NE I-NE D+AJ V P D+N P V AJ P+PRO".split(" " );

        Assert.assertArrayEquals(tags, expected);
    }
}
