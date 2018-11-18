package org.c4i.nlp.match;

import java.util.Map;
import java.util.TreeMap;

/**
 * Character sets for different language types.
 * http://jrgraphix.net/r/Unicode/
 * @author Arvid Halma
 * @version 20-04-2017 - 2:11
 */
public class CharRangeUtil {

    public static Map<String, char[]> RANGES = new TreeMap<>();
    
    static {
        // custom subranges from "Basic Latin"
        cr('A', 'Z', "Basic Latin Uppercase");
        cr('a', 'z', "Basic Latin Lowercase");
        cr('\u00c0', '\u00ff', "Basic Latin Accents");
        cr('0', '9', "Digits");


        // official unicode

        cr('\u0020', '\u007F', "Basic Latin");
        cr('\u00A0', '\u00FF', "Latin-1 Supplement");
        cr('\u0100', '\u017F', "Latin Extended-A");
        cr('\u0180', '\u024F', "Latin Extended-B");
        cr('\u0250', '\u02AF', "IPA Extensions");
        cr('\u02B0', '\u02FF', "Spacing Modifier Letters");
        cr('\u0300', '\u036F', "Combining Diacritical Marks");
        cr('\u0370', '\u03FF', "Greek and Coptic");
        cr('\u0400', '\u04FF', "Cyrillic");
        cr('\u0500', '\u052F', "Cyrillic Supplementary");
        cr('\u0530', '\u058F', "Armenian");
        cr('\u0590', '\u05FF', "Hebrew");
        cr('\u0600', '\u06FF', "Arabic");
        cr('\u0700', '\u074F', "Syriac");
        cr('\u0750', '\u07FF', "Arabic Supplement");
        cr('\u0780', '\u07BF', "Thaana");
        cr('\u0900', '\u097F', "Devanagari");
        cr('\u0980', '\u09FF', "Bengali");
        cr('\u0A00', '\u0A7F', "Gurmukhi");
        cr('\u0A80', '\u0AFF', "Gujarati");
        cr('\u0B00', '\u0B7F', "Oriya");
        cr('\u0B80', '\u0BFF', "Tamil");
        cr('\u0C00', '\u0C7F', "Telugu");
        cr('\u0C80', '\u0CFF', "Kannada");
        cr('\u0D00', '\u0D7F', "Malayalam");
        cr('\u0D80', '\u0DFF', "Sinhala");
        cr('\u0E00', '\u0E7F', "Thai");
        cr('\u0E80', '\u0EFF', "Lao");
        cr('\u0F00', '\u0FFF', "Tibetan");
        cr('\u1000', '\u109F', "Myanmar");
        cr('\u10A0', '\u10FF', "Georgian");
        cr('\u1100', '\u11FF', "Hangul Jamo");
        cr('\u1200', '\u137F', "Ethiopic");
        cr('\u13A0', '\u13FF', "Cherokee");
        cr('\u1400', '\u167F', "Unified Canadian Aboriginal Syllabics");
        cr('\u1680', '\u169F', "Ogham");
        cr('\u16A0', '\u16FF', "Runic");
        cr('\u1700', '\u171F', "Tagalog");
        cr('\u1720', '\u173F', "Hanunoo");
        cr('\u1740', '\u175F', "Buhid");
        cr('\u1760', '\u177F', "Tagbanwa");
        cr('\u1780', '\u17FF', "Khmer");
        cr('\u1800', '\u18AF', "Mongolian");
        cr('\u1900', '\u194F', "Limbu");
        cr('\u1950', '\u197F', "Tai Le");
        cr('\u19E0', '\u19FF', "Khmer Symbols");
        cr('\u1D00', '\u1D7F', "Phonetic Extensions");
        cr('\u1E00', '\u1EFF', "Latin Extended Additional");
        cr('\u1F00', '\u1FFF', "Greek Extended");
        cr('\u2000', '\u206F', "General Punctuation");
        cr('\u2070', '\u209F', "Superscripts and Subscripts");
        cr('\u20A0', '\u20CF', "Currency Symbols");
        cr('\u20D0', '\u20FF', "Combining Diacritical Marks for Symbols");
        cr('\u2100', '\u214F', "Letterlike Symbols");
        cr('\u2150', '\u218F', "Number Forms");
        cr('\u2190', '\u21FF', "Arrows");
        cr('\u2200', '\u22FF', "Mathematical Operators");
        cr('\u2300', '\u23FF', "Miscellaneous Technical");
        cr('\u2400', '\u243F', "Control Pictures");
        cr('\u2440', '\u245F', "Optical Character Recognition");
        cr('\u2460', '\u24FF', "Enclosed Alphanumerics");
        cr('\u2500', '\u257F', "Box Drawing");
        cr('\u2580', '\u259F', "Block Elements");
        cr('\u25A0', '\u25FF', "Geometric Shapes");
        cr('\u2600', '\u26FF', "Miscellaneous Symbols");
        cr('\u2700', '\u27BF', "Dingbats");
        cr('\u27C0', '\u27EF', "Miscellaneous Mathematical Symbols-A");
        cr('\u27F0', '\u27FF', "Supplemental Arrows-A");
        cr('\u2800', '\u28FF', "Braille Patterns");
        cr('\u2900', '\u297F', "Supplemental Arrows-B");
        cr('\u2980', '\u29FF', "Miscellaneous Mathematical Symbols-B");
        cr('\u2A00', '\u2AFF', "Supplemental Mathematical Operators");
        cr('\u2B00', '\u2BFF', "Miscellaneous Symbols and Arrows");
        cr('\u2E80', '\u2EFF', "CJK Radicals Supplement");
        cr('\u2F00', '\u2FDF', "Kangxi Radicals");
        cr('\u2FF0', '\u2FFF', "Ideographic Description Characters");
        cr('\u3000', '\u303F', "CJK Symbols and Punctuation");
        cr('\u3040', '\u309F', "Hiragana");
        cr('\u30A0', '\u30FF', "Katakana");
        cr('\u3100', '\u312F', "Bopomofo");
        cr('\u3130', '\u318F', "Hangul Compatibility Jamo");
        cr('\u3190', '\u319F', "Kanbun");
        cr('\u31A0', '\u31BF', "Bopomofo Extended");
        cr('\u31F0', '\u31FF', "Katakana Phonetic Extensions");
        cr('\u3200', '\u32FF', "Enclosed CJK Letters and Months");
        cr('\u3300', '\u33FF', "CJK Compatibility");
        cr('\u3400', '\u4DBF', "CJK Unified Ideographs Extension A");
        cr('\u4DC0', '\u4DFF', "Yijing Hexagram Symbols");
        cr('\u4E00', '\u9FFF', "CJK Unified Ideographs");
        cr('\uA000', '\uA48F', "Yi Syllables");
        cr('\uA490', '\uA4CF', "Yi Radicals");
        cr('\uAC00', '\uD7AF', "Hangul Syllables");
        cr('\uD800', '\uDB7F', "High Surrogates");
        cr('\uDB80', '\uDBFF', "High Private Use Surrogates");
        cr('\uDC00', '\uDFFF', "Low Surrogates");
        cr('\uE000', '\uF8FF', "Private Use Area");
        cr('\uF900', '\uFAFF', "CJK Compatibility Ideographs");
        cr('\uFB00', '\uFB4F', "Alphabetic Presentation Forms");
        cr('\uFB50', '\uFDFF', "Arabic Presentation Forms-A");
        cr('\uFE00', '\uFE0F', "Variation Selectors");
        cr('\uFE20', '\uFE2F', "Combining Half Marks");
        cr('\uFE30', '\uFE4F', "CJK Compatibility Forms");
        cr('\uFE50', '\uFE6F', "Small Form Variants");
        cr('\uFE70', '\uFEFF', "Arabic Presentation Forms-B");
        cr('\uFF00', '\uFFEF', "Halfwidth and Fullwidth Forms");
        cr('\uFFF0', '\uFFFF', "Specials");
        //cr('\u10000', '\u1007F', "Linear B Syllabary");
        //cr('\u10080', '\u100FF', "Linear B Ideograms");
        //cr('\u10100', '\u1013F', "Aegean Numbers");
        //cr('\u10300', '\u1032F', "Old Italic");
        //cr('\u10330', '\u1034F', "Gothic");
        //cr('\u10380', '\u1039F', "Ugaritic");
        //cr('\u10400', '\u1044F', "Deseret");
        //cr('\u10450', '\u1047F', "Shavian");
        //cr('\u10480', '\u104AF', "Osmanya");
        //cr('\u10800', '\u1083F', "Cypriot Syllabary");
        //cr('\u1D000', '\u1D0FF', "Byzantine Musical Symbols");
        //cr('\u1D100', '\u1D1FF', "Musical Symbols");
        //cr('\u1D300', '\u1D35F', "Tai Xuan Jing Symbols");
        //cr('\u1D400', '\u1D7FF', "Mathematical Alphanumeric Symbols");
        //cr('\u20000', '\u2A6DF', "CJK Unified Ideographs Extension B");
        //cr('\u2F800', '\u2FA1F', "CJK Compatibility Ideographs Supplement");
    }

    public static char[][] LATIN = {
            RANGES.get("Basic Latin Uppercase"),
            RANGES.get("Basic Latin Lowercase"),
            RANGES.get("Basic Latin Accents"),
            RANGES.get("Digits"),
    };

    private static void cr(char a, char b, String name){
        RANGES.put(name, new char[]{a,b});
    }
}
