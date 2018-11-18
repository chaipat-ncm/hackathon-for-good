package org.c4i.chitchat;

import java.util.Arrays;

/**
 * @author Arvid Halma
 * @version 11-11-2017 - 18:16
 */
public class RegexI18NDemo {
    public static void main(String[] args) {
        String[] test = {"Jean-Marie Le'Blanc", "Żółć", "Ὀδυσσεύς", "Ὀδυσσεύς899", "原田雅彦"};
        for (String str : test) {
            System.out.println("\nstr = " + str);
            System.out.print("(?U)\\p{Alpha} = \t");
            System.out.println(str.matches("^(?U)[\\p{Alpha}\\-'. ]+") + " ");
            System.out.print("(?U)\\w = \t");
            System.out.println(str.matches("^(?U)[\\w\\-'. ]+") + " ");
            System.out.print("\\w = \t");
            System.out.println(str.matches("^[\\w\\-'. ]+") + " ");
        }
    }
}
