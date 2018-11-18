package org.c4i.nlp.ner;

import org.c4i.nlp.StopWords;
import org.c4i.nlp.match.Literal;
import org.c4i.nlp.match.Range;
import org.c4i.nlp.match.EntityPlugin;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.tokenize.Token;
import org.c4i.nlp.tokenize.Tokenizer;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Find datetime values
 * @author Arvid Halma
 * @version 18-10-2017 - 15:02
 */
public class DateTimeFinder implements EntityPlugin {
    DataSheet relativeTimeFinder;

    public DateTimeFinder(File file, StringNormalizer normalizer, Tokenizer tokenizer) throws IOException {
        this.relativeTimeFinder = new DataSheet("DATETIME", file, normalizer, tokenizer, StopWords.EMPTY_STOPWORDS);
    }

    @Override
    public boolean accept(Literal lit) {
        return lit.getTokens()[0].getWord().equals("DATETIME");
    }

    @Override
    public List<Range> find(Token[] tokens, Literal lit, String label, int location) {
        DateTime now = DateTime.now();
        List<Range> ranges = relativeTimeFinder.find(tokens, lit, label, location);
        if (!ranges.isEmpty()) {
            // try dict
            for (Range range : ranges) {
                Period hourdiff = Period.hours(Integer.valueOf(range.props.get("hourdiff")));
                range.props.put("type", "datetime");
                range.props.put("now", now.toString());
                range.props.put("timestamp", now.plus(hourdiff).toString());
                range.label = label;
            }
        } else {
            // try date parsing : num month|hour
//            NumberFinder numberFinder = new NumberFinder();
            RegexFinder numberFinder = new RegexFinder(Pattern.compile("^\\d+\\b"), "NUMBER");
            List<Range> numberRange = numberFinder.find(tokens, lit, label, location);
            if (!numberRange.isEmpty() && location + 1 < tokens.length) {
                Token token = tokens[location + 1];
                Range range = new Range("DATETIME", location, location + 2, tokens[location].getCharStart(), token.getCharEnd());
                range.props.put("type", "datetime");
                range.props.put("now", now.toString());
                String normalizedWord = token.getNormalizedWord();
                if (normalizedWord.length() >= 3) {
                    int year = now.year().get();

                    int hour = (int)Double.parseDouble(numberRange.get(0).props.get("number"));
                    switch (normalizedWord) {
                        case "am":
                            range.props.put("timestamp", now.withTime(hour, 0, 0, 0).toString());
                            ranges.add(range);
                            break;

                        case "pm":
                            range.props.put("timestamp", now.withTime(hour + 12, 0, 0, 0).toString());
                            ranges.add(range);
                            break;

                        case "o'clock":
                        case "oclock":
                            range.props.put("timestamp", now.withTime(hour <= 6 ? hour + 12 : hour, 0, 0, 0).toString()); // prefer daylight
                            ranges.add(range);
                            break;
                    }

                    int day = (int)Double.parseDouble(numberRange.get(0).props.get("number"));
                    if (day > 0 && day <= 31) {
                        switch (normalizedWord.substring(0, 3)) {
                            case "jan":
                                range.props.put("timestamp", new DateTime(year, 1, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "feb":
                                range.props.put("timestamp", new DateTime(year, 2, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "fev":
                                range.props.put("timestamp", new DateTime(year, 2, day, 0, 0).toString());
                                ranges.add(range);
                                break; // FR
                            case "mar":
                                range.props.put("timestamp", new DateTime(year, 3, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "apr":
                                range.props.put("timestamp", new DateTime(year, 4, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "avr":
                                range.props.put("timestamp", new DateTime(year, 4, day, 0, 0).toString());
                                ranges.add(range);
                                break; // FR
                            case "may":
                                range.props.put("timestamp", new DateTime(year, 5, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "mai":
                                range.props.put("timestamp", new DateTime(year, 5, day, 0, 0).toString());
                                ranges.add(range);
                                break; // FR
                            case "jun":
                                range.props.put("timestamp", new DateTime(year, 6, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "jul":
                                range.props.put("timestamp", new DateTime(year, 7, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "aug":
                                range.props.put("timestamp", new DateTime(year, 8, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "sep":
                                range.props.put("timestamp", new DateTime(year, 9, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "oct":
                                range.props.put("timestamp", new DateTime(year, 10, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "nov":
                                range.props.put("timestamp", new DateTime(year, 11, day, 0, 0).toString());
                                ranges.add(range);
                                break;
                            case "dec":
                                range.props.put("timestamp", new DateTime(year, 12, day, 0, 0).toString());
                                ranges.add(range);
                                break;

                        }
                    }

                }
            }

            if (!ranges.isEmpty()) {
                return ranges;
            }

            // pattern: month num
            Token token = tokens[location];
            String normalizedWord = token.getNormalizedWord();

            if (normalizedWord.length() >= 3) {

                int year = now.year().get();
                int month = -1;
                switch (normalizedWord.substring(0, 3)) {
                    case "jan":
                        month = 1;
                        break;
                    case "feb":
                        month = 2;
                        break;
                    case "fev":
                        month = 2;
                        break; // FR
                    case "mar":
                        month = 3;
                        break;
                    case "apr":
                        month = 4;
                        break;
                    case "avr":
                        month = 4;
                        break; // FR
                    case "may":
                        month = 5;
                        break;
                    case "mai":
                        month = 5;
                        break; // FR
                    case "jun":
                        month = 6;
                        break;
                    case "jul":
                        month = 7;
                        break;
                    case "aug":
                        month = 8;
                        break;
                    case "sep":
                        month = 9;
                        break;
                    case "oct":
                        month = 10;
                        break;
                    case "nov":
                        month = 11;
                        break;
                    case "dec":
                        month = 12;
                        break;

                }
                if (month > 0 && location + 1 < tokens.length) {
                    numberRange = numberFinder.find(tokens, lit, label, location + 1);
                    if (!numberRange.isEmpty() && location + 1 < tokens.length) {
                        int day = (int)Double.parseDouble(numberRange.get(0).props.get("number"));
                        if(day > 0 && day <= 31) {
                            Range range = new Range("DATETIME", location, location + 2, token.getCharStart(), numberRange.get(0).charEnd);
                            range.props.put("type", "datetime");
                            range.props.put("now", now.toString());
                            range.props.put("timestamp", new DateTime(year, month, day, 0, 0).toString());
                            ranges.add(range);
                        }
                    }
                }
            }
        }
        return ranges;
    }

    @Override
    public String description() {
        return "DATETIME";
    }

    @Override
    public String toString() {
        return "DateTimeFinder{}";
    }
}
