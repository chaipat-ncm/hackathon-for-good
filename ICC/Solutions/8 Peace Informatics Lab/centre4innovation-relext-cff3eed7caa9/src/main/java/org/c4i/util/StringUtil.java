package org.c4i.util;

import java.util.HashMap;

/**
 * Utilities for Strings.
 *
 * @author Arvid Halma
 *         Date: 10-jan-2009
 *         Time: 12:32:13
 */
public class StringUtil {

    /**
     * Check if string starts with an uppercase char.
     * @param s input string
     * @return true if the first character is in capitals
     */
    /*public static boolean startsWithUpperCase(String s){
        return s != null && !s.isEmpty() && Character.isUpperCase(s.charAt(0));
    }*/

    public static boolean startsWithUpperCase(String s){
        final char c = s.charAt(0);
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Check if string starts with a digit (0-9).
     * @param s input string
     * @return true if the first character is a number
     */
    public static boolean startsWithDigit(String s){
        return s != null && !s.isEmpty() && Character.isDigit(s.charAt(0));
    }

    /**
     * Limit the text to be at most n chars
     * @param s input string
     * @param n the max length
     * @return the possibly shortened string
     */
    public static String truncate(String s, int n){
        return s.substring(0, Math.min(s.length(), n));
    }

    /**
     * This String util method removes single or double quotes
     * from a string if its quoted.
     * for input string = "mystr1" output will be = mystr1
     * for input string = 'mystr2' output will be = mystr2
     *
     * @param s to be unquoted.
     * @return value unquoted, null if input is null.
     */
    public static String unquote(String s) {
        if (s != null && s.length() >= 2
                && ((s.startsWith("\"") && s.endsWith("\""))
                || (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }


    /**
     * Number of sequentially overlapping characters.
     * @param source  the first String, must not be null
     * @param target  the second String, must not be null
     * @return overlap (with gaps) score
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static int levenshteinSimilarity(String source, String target) {
        return Math.max(source.length(), target.length()) - levenshteinDistance(source, target);
    }

    /**
     * Number of sequentially overlapping characters/relative .
     * @param source  the first String, must not be null
     * @param target  the second String, must not be null
     * @return overlap (with gaps) score
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static double levenshteinMatchScore(String source, String target) {
        double sim = levenshteinSimilarity(source, target);

        return sim == 0 ? 0 : sim / Math.max(source.length(), target.length());
    }

    /**
     * <p>Find the Levenshtein distance between two Strings.</p>
     *
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     *
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     *
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError
     * which can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a></p>
     *
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","")               = 0
     * StringUtils.getLevenshteinDistance("","a")              = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param source  the first String, must not be null
     * @param target  the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static int levenshteinDistance(String source, String target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
          The difference between this impl. and the previous is that, rather
          than creating and retaining a matrix of size s.length()+1 by t.length()+1,
          we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
          is the 'current working' distance array that maintains the newest distance cost
          counts as we iterate through the characters of String s.  Each time we increment
          the index of String t we are comparing, d is copied to p, the second int[].  Doing so
          allows us to retain the previous cost counts as required by the algorithm (taking
          the minimum of the cost count to the left, up one, and diagonally up and to the left
          of the current cost count being calculated).  (Note that the arrays aren't really
          copied anymore, just switched...this is clearly much better than cloning an array
          or doing a System.arraycopy() each time  through the outer loop.)

          Effectively, the difference between the two implementations is this one does not
          cause an out of memory condition when calculating the LD over two very large strings.
        */

        int n = source.length(); // length of s
        int m = target.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            String tmp = source;
            source = target;
            target = tmp;
            n = m;
            m = target.length();
        }

        int p[] = new int[n+1]; //'previous' cost array, horizontally
        int d[] = new int[n+1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i<=n; i++) {
            p[i] = i;
        }

        for (j = 1; j<=m; j++) {
            t_j = target.charAt(j-1);
            d[0] = j;

            for (i=1; i<=n; i++) {
                cost = source.charAt(i-1)==t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * In information theory and computer science, the Damerau–Levenshtein distance
     * (named after Frederick J. Damerau and Vladimir I. Levenshtein) is a "distance" (string metric) between two strings,
     * i.e., finite sequence of symbols, given by counting the minimum number of operations needed to transform one string
     * into the other, where an operation is defined as an insertion, deletion, or substitution of a single character,
     * or a transposition of two adjacent characters. In his seminal paper[1],
     * Damerau not only distinguished these four edit operations but also stated that they correspond to more than
     * 80% of all human misspellings. Damerau's paper considered only misspellings that could be corrected with at most
     * one edit operation. The corresponding edit distance, i.e., dealing with multiple edit operations, known as the
     * Levenshtein distance, was introduced by Levenshtein,[2] but it did not include transpositions in the set of
     * basic operations. The name Damerau–Levenshtein distance is used to refer to the edit distance that allows
     * multiple edit operations including transpositions, although it is not reset whether the term Damerau–Levenshtein
     * distance is sometimes used in some sources as to take into account non-adjacent transpositions or not.
     *
     * While the original motivation was to measure distance between human misspellings to improve applications such
     * as spell checkers, Damerau–Levenshtein distance has also seen uses in biology to measure the variation between DNA.
     *
     * @param source  the first String
     * @param target  the second String
     * @return result distance
     */
    public static int damerauLevenshteinDistance(String source, String target){

        if (source == null || source.isEmpty()){
            if (target == null || target.isEmpty()){
                return 0;
            }
            else {
                return target.length();
            }
        }
        else if (target == null || target.isEmpty()){
            return source.length();
        }

        int sLen = source.length();
        int tLen = target.length();

        int[][] score = new int[sLen + 2][tLen + 2];

        int INF = sLen + tLen;
        score[0][0] = INF;
        for (int i = 0; i <= sLen; i++) { score[i + 1][ 1] = i; score[i + 1][ 0] = INF; }
        for (int j = 0; j <= tLen; j++) { score[1][ j + 1] = j; score[0][ j + 1] = INF; }

        HashMap<Character, Integer> sd = new HashMap<Character, Integer>();
        for (char c : (source + target).toCharArray()) {
            if(!sd.containsKey(c)){
                sd.put(c, 0);
            }
        }

        for (int i = 1; i <= sLen; i++){
            int DB = 0;
            for (int j = 1; j <= tLen; j++){
                int i1 = sd.get(target.charAt(j - 1));
                int j1 = DB;

                if (source.charAt(i - 1) == target.charAt(j - 1)){
                    score[i + 1][j + 1] = score[i][j];
                    DB = j;
                }
                else{
                    score[i + 1][j + 1] = Math.min(score[i][j], Math.min(score[i + 1][j], score[i][j + 1])) + 1;
                }

                score[i + 1][j + 1] = Math.min(score[i + 1][j + 1], score[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
            }

            sd.put(source.charAt(i - 1), i);
        }

        return score[sLen + 1][tLen + 1];
    }


    /**
     * Remove single line comments (ignoring comments in quoted string).
     * A regex approach should also work, but in practice runs into stackoverflows.
     * Pattern COMMENT_PATTERN = Pattern.compile("(?:\"[^\"]*\"|[^\"#])*#.*");
     *
     * https://codegolf.stackexchange.com/questions/48326/remove-single-line-and-multiline-comments-from-string
     * @param source input/source
     * @param lineComment a marker like "//" or "#" to indicate the start of a line comment
     * @return the source without comments
     */
    public static String removeLineComments(String source, String lineComment) {
        final int DEFAULT = 1,
                ESCAPE = 2, ESCAPE2 = 22,
                STRING = 3, STRING2 = 32,
                LINE_COMMENT = 40;

        StringBuilder out = new StringBuilder();
        int mod = DEFAULT;
        int commentLength = lineComment.length();
        for (int i = 0; i < source.length(); i++) {
            String substring = source.substring(i, Math.min(i + commentLength, source.length()));
            char c = source.charAt(i);
            switch (mod) {
                case DEFAULT: // default
                    if (substring.equals(lineComment)) {
                        mod = LINE_COMMENT;
                    } else {
                        if (c == '"') mod = STRING;
//                        else if (c == '\'') mod = STRING2;
                        else mod = DEFAULT;
                    }
                    break;
                case STRING: // "string"
                    mod = c == '"' ? DEFAULT : c == '\\' ? ESCAPE : STRING;
                    break;
                /*case STRING2: // 'string'
                    mod = c == '\'' ? DEFAULT : c == '\\' ? ESCAPE2 : STRING2;
                    break;*/
                case ESCAPE: // "string"
                    mod = STRING;
                    break;
                /*case ESCAPE2: // 'string'
                    mod = STRING2;
                    break;*/
                case LINE_COMMENT: // single line comment
                    mod = c == '\n' ? DEFAULT : LINE_COMMENT;
                    break;
            }
            out.append(mod < LINE_COMMENT ? c : "");
        }

        return out.toString();
    }

    public static String removeJavaComments(String s) {
        final int DEFAULT = 1,
                ESCAPE = 2,
                STRING = 3,
                ONE_LINE_COMMENT = 4, MULTI_LINE_COMMENT = 5;

        StringBuilder out = new StringBuilder();
        int mod = DEFAULT;
        for (int i = 0; i < s.length(); i++) {
            String substring = s.substring(i, Math.min(i + 2 , s.length()));
            char c = s.charAt(i);
            switch (mod) {
                case DEFAULT: // default
                    mod = substring.equals("/*") ? MULTI_LINE_COMMENT : substring.equals("//") ? ONE_LINE_COMMENT : c == '"' ? STRING : DEFAULT;
                    break;
                case STRING: // string
                    mod = c == '"' ? DEFAULT : c == '\\' ? ESCAPE : STRING;
                    break;
                case ESCAPE: // string
                    mod = STRING;
                    break;
                case ONE_LINE_COMMENT: // one line comment
                    mod = c == '\n' ? DEFAULT : ONE_LINE_COMMENT;
                    continue;
                case MULTI_LINE_COMMENT: // multi line comment
                    mod = substring.equals("*/") ? DEFAULT : MULTI_LINE_COMMENT;
                    i += mod == DEFAULT ? 1 : 0;
                    continue;
            }
            out.append(mod < 4 ? c : "");
        }

        return out.toString();
    }


}
