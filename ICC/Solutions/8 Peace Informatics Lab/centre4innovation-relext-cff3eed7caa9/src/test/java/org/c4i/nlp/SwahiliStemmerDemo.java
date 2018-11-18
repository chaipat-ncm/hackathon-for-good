package org.c4i.nlp;

import org.c4i.nlp.normalize.*;

import java.io.File;
import java.io.IOException;

public class SwahiliStemmerDemo {
    static SwahiliStemmer stemmer5;

    static {
        try {
            stemmer5 = new SwahiliStemmer(new File("data/nlp/sw/sw-stem.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
/*
        Now get rid of plurals and -ed or -ing. e.g.
                they cooked:  walipikia  -> walipik~
                pay attention: walipikiana ->  walipik
        they were taken: walichukuliwa -> walichuku*/

        stem("amesema"); // sema
        stem("amezungumzia"); // zungumza
        stem("kuendesha"); // endelea
        stem("kumuua"); // ua
        stem("walipikia"); // pika
        stem("walipikiana"); // pikia
        stem("walichukuliwa"); // chukua
//        stem("");
//        stem("");
//        stem("");



    }

    private static void stem(String w){
        System.out.println(w + " 5 => " + stemmer5.normalize(w));
    }
}
