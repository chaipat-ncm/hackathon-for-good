package org.c4i.chitchat;

import com.google.common.collect.ImmutableMap;
import org.c4i.nlp.match.Substitution1Way;

public class SubstituteDemo {
    public static void main(String[] args) {
        ImmutableMap<String, String> replacements = ImmutableMap.of("aa", "bb", "cc", "dd");
        String orgStr = "blaa aa sfa cc hsdhf aa";
        System.out.println("orgStr = " + orgStr);
        String newStr = new Substitution1Way(replacements, false).apply(orgStr);
        System.out.println("newStr = " + newStr);
        String newSt2 = new Substitution1Way("aa\tbb\r\ncc\tdd\n", false).apply(orgStr);
        System.out.println("newSt2 = " + newSt2);

    }
}
