package org.c4i.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for parsing and representing dates and times.
 * @version 10-8-16
 * @author Arvid Halma
 */
public class DateTimeUtil {

    // All values do fin in ints, but are longs to prevent overflows during computation
    private static final long
            ONE_SECOND = 1000,
            ONE_MINUTE = 60 * ONE_SECOND,
            ONE_HOUR = 60 * ONE_MINUTE,
            ONE_DAY = 24 * ONE_HOUR,
            ONE_WEEK = 7 * ONE_DAY;


    // DateTimeFormat is thread-safe and immutable, and the formatters it returns are as well.
    // http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html

    private static final List<DateTimeFormatter> dateFormatters = Arrays.asList(
            DateTimeFormat.forPattern("dd-MM-yyyy"),
            DateTimeFormat.forPattern("yyyy-MM-dd")
    );

    private static final List<DateTimeFormatter> dateTimeFormatters = Arrays.asList(
            DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss a"),
            DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormat.forPattern("dd-MM-yyyy, HH:mm")
    );

    private static final List<DateTimeFormatter> dateTimeZoneFormatters = Arrays.asList(
            DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ssZ"),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ"),
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
    );

    private static final List<DateTimeFormatter> allDateTimeFormatters;
    private static final List<DateTimeFormatter> allFormatters;

    public static final DateTimeFormatter isoWithFractionalSeconds = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm:ss")
            .appendLiteral('.')
            .appendFractionOfSecond(1, 9)

            .toFormatter();

    static {
        allDateTimeFormatters = new ArrayList<>();
        allDateTimeFormatters.addAll(dateTimeZoneFormatters);
        allDateTimeFormatters.addAll(dateTimeFormatters);

        allFormatters = new ArrayList<>();
        allFormatters.addAll(allDateTimeFormatters);
        allFormatters.addAll(dateFormatters);
    }

    private static DateTimeFormatter outputDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter outputDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ");

    public static String toDateString(DateTime dt){
        return outputDateFormatter.print(dt);
    }

    public static String toDateTimeString(DateTime dt){
        return outputDateTimeFormatter.print(dt);
    }

    public static DateTime parseLiberalDate(String date) {
        if(date == null)
            return null;
        date = date.replace('/', '-');
        for (DateTimeFormatter dateTimeFormatter : dateFormatters) {
            try {
                return dateTimeFormatter.parseDateTime(date);
            } catch (IllegalArgumentException ignored) {}
        }
        throw new IllegalArgumentException(String.format("\"%s\" could not be parsed as valid DateTime.", date));
    }

    public static DateTime parseLiberalDateTime(String date, String time) {
        if(date == null)
            return null;
        date = date.replace('/', '-');
        if (time == null) {
            return parseLiberalDate(date);
        } else {
            String datetime = date + " " + time;
            for (DateTimeFormatter dateTimeFormatter : allDateTimeFormatters) {
                try {
                    return dateTimeFormatter.parseDateTime(datetime);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        throw new IllegalArgumentException(String.format("(\"%s\", \"%s\") could not be parsed as valid DateTime.", date, time));
    }

    public static DateTime parseLiberalDateTime(String date, String time, String defaultTimeZone) {
        return parseLiberalDateTime(date, time, DateTimeZone.forID(defaultTimeZone));
    }

    public static DateTime parseLiberalDateTime(String date, String time, DateTimeZone defaultTimeZone) {
        if(date == null)
            return null;
        date = date.replace('/', '-');
        if (time == null) {
            return parseLiberalDate(date).withZoneRetainFields(defaultTimeZone);
        } else {
            String datetime = date + " " + time;
            // try times without zone info: use default time zone arg
            for (DateTimeFormatter dtf : dateTimeFormatters) {
                try {
                    return dtf.parseDateTime(datetime).withZoneRetainFields(defaultTimeZone);
                } catch (IllegalArgumentException ignored) {}
            }
            // try times with explicit: don't use default time zone arg
            for (DateTimeFormatter dtfTz : dateTimeZoneFormatters) {
                try {
                    return dtfTz.parseDateTime(datetime);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        throw new IllegalArgumentException(String.format("(\"%s\", \"%s\") could not be parsed as valid DateTime.", date, time));
    }

    public static DateTime parseLiberalDateTime(String datetime) {
        if(datetime == null)
            return null;
        datetime = datetime.replace('/', '-');
        if (datetime.length() <= 10) {
            return parseLiberalDate(datetime);
        } else {
            for (DateTimeFormatter dateTimeFormatter : allDateTimeFormatters) {
                try {
                    return dateTimeFormatter.parseDateTime(datetime);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        throw new IllegalArgumentException(String.format("\"%s\" could not be parsed as valid DateTime.", datetime));
    }

    public static DateTime parseLiberalDateTime(String datetime, DateTimeZone defaultTimeZone) {
        if(datetime == null)
            return null;
        datetime = datetime.replace('/', '-');
        if (datetime.length() <= 10) {
            return parseLiberalDate(datetime).withZoneRetainFields(defaultTimeZone);
        } else {
            // try times without zone info: use default time zone arg
            for (DateTimeFormatter dtf : dateTimeFormatters) {
                try {
                    return dtf.parseDateTime(datetime).withZoneRetainFields(defaultTimeZone);
                } catch (IllegalArgumentException ignored) {}
            }
            // try times with explicit: don't use default time zone arg
            for (DateTimeFormatter dtfTz : dateTimeZoneFormatters) {
                try {
                    return dtfTz.parseDateTime(datetime);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        throw new IllegalArgumentException(String.format("\"%s\" could not be parsed as valid DateTime.", datetime));
    }


    /**
     * Pretty print time durations. Especially handle recent times, i.e. withing 24 h ago.
     * @param then some moment
     * @return a formatted string
     */
    public static String prettyTimeString(long then){
        long now = System.currentTimeMillis();

        // date: days since epoch
        long dateNow = now / ONE_DAY;
        long dateThen = then / ONE_DAY;
        boolean yesterdayDate = dateThen + 1 == dateNow;

        long milliseconds, seconds, minutes, hours;
        milliseconds = now - then;
        seconds = milliseconds / 1000;
        minutes = seconds / 60;
        seconds -= minutes * 60;
        hours = minutes / 60;
        minutes -= hours * 60;

        if (hours < 24) {
            if (hours > 6 && yesterdayDate) {
                return "yesterday";
            }
            String hourString = ""+hours+ ((hours == 1) ? " hour" : " hours");
            String minuteString = ""+minutes+ ((minutes == 1) ? " minute" : " minutes");
            String secondString = ""+seconds+ ((seconds == 1) ? " second" : " seconds");
            if (hours > 0){
                if (minutes == 0) return hourString + " ago";
                else return hourString + " and " + minuteString + " ago";
            }
            else if (minutes > 0){
                if (minutes >= 5) return minuteString + " ago";
                else return minuteString + " and " + secondString + " ago";
            }
            else return secondString + " ago";
        }

        return dateFormatters.get(0).print(new DateTime(then));
    }
}
