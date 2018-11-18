package org.c4i.nlp.normalize;

/**
 * A Metaphone3 normalizer that only is applied for words longer than a given length.
 * This reduces some ambiguity (otherwise both "hi" and "he" become "H").
 * @author Arvid Halma
 * @version 7-9-18
 */
public class Metaphone3Long implements StringNormalizer{
    private int minLength;
    private Metaphone3 metaphone3;

    public Metaphone3Long() {
        this(4);
    }

    public Metaphone3Long(int minLength) {
        this.minLength = minLength;
        this.metaphone3 = new Metaphone3();
    }

    @Override
    public String normalize(String string) {
        return string.length() >= minLength ? metaphone3.normalize(string) : string;
    }


    @Override
    public String toString() {
        return "Metaphone3Long";
    }
}
