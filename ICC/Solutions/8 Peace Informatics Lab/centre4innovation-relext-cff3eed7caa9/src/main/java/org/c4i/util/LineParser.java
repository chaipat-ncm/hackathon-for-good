package org.c4i.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Iterate and process each line in a (list of) input streams.
 * Includes CSV parser.
 * @author Arvid
 * @version 13-10-2015 - 21:07
 */
public class LineParser {

    public static void lines(Consumer<String> lineConsumer, File file) throws IOException {
        lines(lineConsumer, "UTF-8", file);
    }

    public static void lines(Consumer<String> lineConsumer, String encoding, File file) throws IOException {
        lines(lineConsumer, encoding, new FileInputStream(file));
    }

    public static void lines(Consumer<String> lineConsumer, String encoding, InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                // line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    // lines line
                    lineConsumer.accept(line);
                }
            }
        }
    }

    public static Iterator<String> linesIter(String encoding, InputStream is) throws IOException {
        return new RowIterator(new BufferedReader(new InputStreamReader(is, encoding)));
    }

    public static void tsvRows(Consumer<String[]> rowConsumer, File file) throws IOException {
        csvRows("[\t]", true, rowConsumer, "UTF-8", true, file);
    }

    public static void tsvRows(Consumer<String[]> rowConsumer, InputStream is) throws IOException {
        csvRows("[\t]", true, rowConsumer, "UTF-8", true, is);
    }

    public static RecordIterator<String[]> tsvRecordIterator(InputStream is) throws IOException {
        return new RecordIterator<>(is, vals -> vals);
    }

    public static void csvRows(Consumer<String[]> rowConsumer, File file) throws IOException {
        csvRows("[,]", true, rowConsumer, "UTF-8", true, file);
    }

    public static void csvRows(Consumer<String[]> rowConsumer, InputStream is) throws IOException {
        csvRows("[,]", true, rowConsumer, "UTF-8", true, is);
    }

    public static void csvRows(String separator, boolean unquote, Consumer<String[]> rowConsumer, String encoding, boolean emptyStringAsNull, File file) throws IOException {
        lines(columnConsumer(unquote, rowConsumer, emptyStringAsNull, separator), encoding, file);
    }

    public static void csvRows(String separator, boolean unquote, Consumer<String[]> rowConsumer, String encoding, boolean emptyStringAsNull, InputStream is) throws IOException {
        lines(columnConsumer(unquote, rowConsumer, emptyStringAsNull, separator), encoding, is);
    }

    private static Consumer<String> columnConsumer(boolean unquote, Consumer<String[]> rowConsumer, boolean emptyStringAsNull, String separator) {
        Splitter splitter = Splitter.on(Pattern.compile(separator+"(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
        return line -> {
            String[] vals = Iterables.toArray(splitter.split(line), String.class);
            if(unquote) {
                for (int i = 0; i < vals.length; i++) {
                    String v = unquote(vals[i].trim());
                    vals[i] = emptyStringAsNull && v.isEmpty() ? null : v;
                }
                rowConsumer.accept(vals);
            }
        };
    }




    /**
     * This String util method removes single or double quotes
     * from a string if its quoted.
     * for input string = "mystr1" output will be = mystr1
     * for input string = 'mystr2' output will be = mystr2
     *
     * @param s to be unquoted.
     * @return value unquoted, null if input is null.
     *
     */
    public static String unquote(String s) {
        if (s != null && s.length() >= 2
                && ((s.startsWith("\"") && s.endsWith("\""))
                     || (s.startsWith("'") && s.endsWith("'"))))
        {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static double parseSloppyDouble(String s){
        if(s == null){
            return 0.0;
        }
        try {
            return Double.parseDouble(s.replace(',', '.'));
        } catch(NumberFormatException ignored) {
            return 0.0;
        }
    }

    public static Double parseSloppyDoubleObject(String s){
        if(s == null){
            return null;
        }
        try {
            return Double.parseDouble(s.replace(',', '.'));
        } catch(NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Line iterator that skips emty or commented lines.
     */
    public static class RowIterator extends LineIterator<String> {

        public RowIterator(Reader reader) throws IllegalArgumentException {
            super(reader, s -> s);
        }

        @Override
        protected boolean isValidLine(String line) {
            return line != null && !line.isEmpty() && !line.startsWith("#");
        }
    }


    /**
     * Line iterator that yields pojos, given a function that transforms Stringp[] to the pojo.
     * @param <T> the object type to yield.
     */
    public static class RecordIterator<T> extends LineIterator<T> {
        Function<String[], T> recordMapper;
        Consumer<String> onRecordError;

        public RecordIterator(InputStream is, Function<String[], T> recordMapper) throws IllegalArgumentException {
            this(new BufferedReader(new InputStreamReader(is)), Splitter.on(Pattern.compile("[\t](?=([^\"]*\"[^\"]*\")*[^\"]*$)")), recordMapper, s -> {}, true, true);
            this.recordMapper = recordMapper;
        }

        public RecordIterator(Reader reader, Splitter splitter, Function<String[], T> recordMapper, Consumer<String> onRecordError, boolean unquote, boolean emptyStringAsNull) throws IllegalArgumentException {
            super(reader, line -> {
                String[] vals = Iterables.toArray(splitter.split(line), String.class);
                for (int i = 0; i < vals.length; i++) {
                    String v = unquote ? unquote(vals[i]) : vals[i];
                    vals[i] = emptyStringAsNull && v.isEmpty() ? null : v;
                }
                return recordMapper.apply(vals);
            });
            this.recordMapper = recordMapper;
            this.onRecordError = onRecordError;
        }

        public static RecordIterator<String[]> recordIterator(Reader reader, boolean unquote, boolean emptyStringAsNull) throws IllegalArgumentException {
            return recordIterator(reader, "[\t]", unquote, emptyStringAsNull);
        }

        public static RecordIterator<String[]> recordIterator(Reader reader, String splitter,  boolean unquote, boolean emptyStringAsNull) throws IllegalArgumentException {
            return new RecordIterator<>(reader, Splitter.on(Pattern.compile(splitter)), line -> line, line -> {}, unquote, emptyStringAsNull);
        }

        @Override
        protected boolean isValidLine(String line) {
            boolean saneLine = line != null && !line.isEmpty() && !line.startsWith("#");
            if(!saneLine){
                return false;
            }
            try{
                cachedObject = lineConverter.apply(line);
            } catch (Exception e){
                cachedObject = null;
                onRecordError.accept(line);
                return false;
            }
            return true;
        }
    }


    /** Generalized from: org.apache.commons.io.LineIterator */
    public static class LineIterator<T> implements Iterator<T> {
        private final BufferedReader bufferedReader;
        private String cachedLine;
        protected T cachedObject;
        private boolean finished = false;
        protected Function<String, T> lineConverter;

        public LineIterator(Reader reader, Function<String, T> lineConverter) throws IllegalArgumentException {
            this.lineConverter = lineConverter;
            if(reader == null) {
                throw new IllegalArgumentException("Reader must not be null");
            } else {
                if(reader instanceof BufferedReader) {
                    this.bufferedReader = (BufferedReader)reader;
                } else {
                    this.bufferedReader = new BufferedReader(reader);
                }

            }
        }

        public boolean hasNext() {
            if(this.cachedLine != null) {
                return true;
            } else if(this.finished) {
                return false;
            } else {
                try {
                    String ioe;
                    do {
                        ioe = this.bufferedReader.readLine();
                        if(ioe == null) {
                            this.finished = true;
                            return false;
                        }
                    } while(!this.isValidLine(ioe));

                    this.cachedLine = ioe;
                    return true;
                } catch (IOException var2) {
                    this.close();
                    throw new IllegalStateException(var2);
                }
            }
        }

        protected boolean isValidLine(String line) {
            return true;
        }

        public T next() {
            return this.nextLine();
        }

        public T nextLine() {
            if(!this.hasNext()) {
                throw new NoSuchElementException("No more lines");
            } else {
                this.cachedLine = null;
                return cachedObject;
            }
        }

        public void close() {
            this.finished = true;
            try {
                this.bufferedReader.close();
            } catch (IOException ignored) {}

            this.cachedLine = null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove unsupported on LineIterator");
        }


    }
}
