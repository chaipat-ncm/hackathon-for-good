package org.c4i.util;


import java.util.regex.Pattern;

/**
 * Common regex patterns
 * @author Arvid Halma
 * @version 3/16/13
 * Time: 12:10 PM
 */
public class RegexUtil {

    private RegexUtil(){}

    /**
     * Email address. YES: john@doe.com,  NO: john@doe.something (TLD is too long)
     */
    public static final Pattern EMAIL = Pattern.compile("^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$");

    /**
     * Url. YES: http://net.tutsplus.com/about, NO:http://google.com/some/file!.html (contains an exclamation point)
     */
    public static final Pattern URL = Pattern.compile("^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$");

    /**
     * IP address. YES: 73.60.124.136, NO: 256.60.124.136 (the first group must be "25" and a number between zero and five)
     */
    public static final Pattern IP = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * HTML tag with well-formed attributes.
     */
    public static final Pattern HTML_TAG = Pattern.compile("^<([a-z]+)([^<]+)*(?:>(.*)</\\1>|\\s+/>)$");

    /**
     * Extract href link.
     */
    public static final Pattern HTML_HREF = Pattern.compile("\\s*(?i)href\\s*=\\s*(\\\"([^\"]*\\\")|'[^']*'|([^'\">\\s]+))");

    /**
     * Hex color code.
     */
    public static final Pattern HEX_COLOR = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    /**
     * Year 1900-2099
     */
    public static final Pattern YEAR = Pattern.compile("^(19|20)[\\d]{2,2}$");

    /**
     * Date (dd mm yyyy, d/m/yyyy, etc.)
     */
    public static final Pattern DATE_DMY = Pattern.compile("^([1-9]|0[1-9]|[12][0-9]|3[01])\\D([1-9]|0[1-9]|1[012])\\D(19[0-9][0-9]|20[0-9][0-9])$\n" +
            "IP v4 --- ^(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]){3}$");

    /**
     * Number, e.g. -3.4e6
     */
    public static final Pattern NUMBER = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$");

    /**
     * Integer, e.g. -412
     */
    public static final Pattern INTEGER = Pattern.compile("^[-+]?[0-9]+$");

    /**
     * Integer, e.g. 53
     */
    public static final Pattern INTEGER_POSITIVE = Pattern.compile("^[1-9][0-9]*$");

    /**
     * Integer, e.g. 0 or 41
     */
    public static final Pattern INTEGER_NON_NEGATIVE = Pattern.compile("^[0-9]+$");

}