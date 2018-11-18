package org.c4i.nlp.normalize;

/**
 * Metaphone/soundex phonetic string normalization for the Dutch language.
 *
 * Based on Diederik Krols' implementation:
 * https://blogs.u2u.be/diederik/post/Fuzzy-lookup-of-Names-with-a-Dutch-Metaphone-implementation
 * Additional changes: 'z' maps to 's', 'v' maps to 'f'.
 * @author Arvid Halma
 */
public class MetaphoneNL implements StringNormalizer{

    public MetaphoneNL() {}

    /**
     * Transform a string to its normalized form.
     * @param word the input word
     * @return a phonetic similar string
     */
    @Override
    public String normalize(String word) {
        StringBuilder result = new StringBuilder();

        int length = word.length();
        char previousChar, currentChar, nextChar;
        boolean wordStart = true;

        for(int i = 0; i < length; i++) {
            currentChar = Character.toLowerCase(word.charAt(i));

            // non-alpha? skip...
            if (currentChar < 'a' || currentChar > 'z') {
                result.append(' ');
                wordStart = true;
                continue;
            }

            // initial vowel
            if (wordStart && oneOf(currentChar, "aeiou")) {
                result.append('a');
                continue;
            }
            wordStart = false;

            // surrounding chars
            if (i != 0) {
                previousChar = Character.toLowerCase(word.charAt(i - 1));
            } else {
                previousChar = ' ';
            }

            if (previousChar != currentChar && i < length-2) {
                nextChar = Character.toLowerCase(word.charAt(i + 1));
            } else {
                nextChar = ' ';
            }

            // consonants
            switch (currentChar) {
                case 'b':
                    if (i == length - 1) {
                        result.append('p');
                    } else {
                        result.append('b');

                    }
                    break;
                case 'c':
                    if (nextChar == 'h') {
                        if (i == 0) {
                            result.append('x');
                        } else {
                            result.append('g');
                        }
                    } else if (oneOf(nextChar, "iey") && previousChar != 's') {
                        result.append('s');

                    } else {
                        result.append('k');
                    }
                    break;
                case 'd':
                    if (oneOf(word, i, "dge", "dgy", "dgi")) {
                        result.append('j');
                    } else {
                        if (i == length - 1) {
                            result.append('t');
                        } else {
                            result.append('d');
                        }
                    }
                    break;
                case 'f': result.append('f'); break;
                case 'g': result.append('g'); break;
                case 'h':
                    if (!oneOf(previousChar, "csptg") && oneOf(nextChar, "aeiouy")) {
                        result.append('h');
                    }
                    break;
                case 'j':
                    if (!oneOf(previousChar, "is")) {
                        if (previousChar == 't') {
                            result.append('x');
                        } else {
                            result.append('j');
                        }
                    }
                    break;
                case 'k':
                    if (previousChar != 'c') {
                        result.append('k');
                    }
                    break;
                case 'l': result.append('l'); break;
                case 'm': result.append('m'); break;
                case 'n': result.append('n'); break;
                case 'p':
                    if (nextChar == 'h') {
                        result.append('f');
                    } else {
                        result.append('p');
                    }
                    break;
                case 'q': result.append('k'); break;
                case 'r': result.append('r'); break;
                case 's':
                    if (oneOf(nextChar, "hj")) {
                        result.append('x');
                    } else {
                        result.append('s');
                    }
                    break;
                case 't':
                    if (oneOf(word, i, "tia", "tio", "tie", "tiu")) {
                        result.append('s');
                    } else {
                        result.append('t');
                    }
                    break;
                case 'v': result.append('f'); break;
                case 'w':
                    if (nextChar == 'r') {
                        result.append('v');
                    } else {
                        result.append('w');
                    }
                    break;
                case 'x':
                    result.append("ks");
                    break;
                case 'y':
                    if (oneOf(nextChar, "aeiou")) {
                        result.append('j');
                    }
                    break;
                case 'z':
                    result.append('s');
                    break;
            }
        }

        return result.toString();
    }

    /**
     * Check if the given character is one of the provided characters, given as a string.
     * @param c the char to query
     * @param s the 'list' of possible chars
     * @return true, if c is contained in s, false otherwise.
     */
    private static boolean oneOf(char c, String s){
        return s.indexOf(c) >= 0;
    }

    /**
     * Check if the given input string contains one of the given possible substrings at a given location.
     * @param input the string to query
     * @param offset start character offset index
     * @param possibilities the possible substrings
     * @return true if a substring is found, false otherwise.
     */
    private static boolean oneOf(String input, int offset, String... possibilities){
        for (String possibility : possibilities) {
            if(input.startsWith(possibility, offset)){
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "MetaphoneNL";
    }

}
