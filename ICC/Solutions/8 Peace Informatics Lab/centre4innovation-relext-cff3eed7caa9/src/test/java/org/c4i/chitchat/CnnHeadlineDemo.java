package org.c4i.chitchat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author Arvid Halma
 * @version 1-3-18
 */
public class CnnHeadlineDemo {
    public static void main(String[] args) throws IOException {

        Document doc = Jsoup.connect("http://lite.cnn.io/en").get();
        String headline = doc.select("li").first().text();
        System.out.println("headline = " + headline);
    }
}
