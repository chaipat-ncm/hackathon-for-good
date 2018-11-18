package org.c4i.nlp;

import org.c4i.nlp.detect.LanguageDetector;
import org.c4i.nlp.detect.OpenLanguageDetector;

import java.io.File;
import java.io.IOException;

public class OpenLanguageDetectorDemo {

    public static void main(String[] args) throws IOException {
        LanguageDetector detector = new OpenLanguageDetector(new File("data/nlp/langdetect-183.bin"), "en", 200);
        System.out.println(detector.getLanguageCode("This is a small text in a certain language"));
        System.out.println(detector.getLanguageCode("Dit is een kleine tekst in een bepaalde taal\n"));
        System.out.println(detector.getLanguageCode("هذا نص صغير بلغة معينة"));
        System.out.println(detector.getLanguageCode("hadha nasi saghir bilughat mueayana"));
        System.out.println(detector.getLanguageCode("Ceci est un petit texte dans une certaine langue"));
        System.out.println(detector.getLanguageCode("hallo, ik ben nederlands"));
        System.out.println(detector.getLanguageCode("hello"));
        System.out.println(detector.getLanguageCode("hallo"));
        System.out.println(detector.getLanguageCode("hello i's me"));
        System.out.println(detector.getLanguageCode("hallo ik heet arvid"));
        System.out.println(detector.getLanguageCode("ich bin"));
    }


}
