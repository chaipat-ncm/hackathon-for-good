package org.c4i.util;

import com.google.common.base.Splitter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Csv reader/writer utilities.
 *
 * The streaming API makes parsing CSV files easier...
 * <pre>
 *     try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding))) {
 reader.lines()
 .skip(skip)
 .limit(limit)
 .filter(line -&gt; !line.startsWith("#"))
 .map(line -&gt; splitter.splitToList(line))
 .forEach(consumer);
 * </pre>
 * ... but this class provides additional functionality:
 * <ul>
 *     <li> robust column parser: handle quoted delimiters well
 *     <li> use column names in getters (e.g. row.getInt("age"))
 *     <li> column name normalization when desired (e.g. row.getInt("age") == row.getInt("AGE"))
 *     <li> automatic statistics: success, error, skipped, meta, total line counts.</li>
 *     <li> iterators: get rows as an iterator (handy when other APIs require this)
 *     <li> custom predicates for ignoring certain lines
 *     <li> custom predicates to stop processing (e.g. when all lines are empty)
 *     <li> set initial offset to skip first n lines.
 *     <li> set maximum number of lines.
 *     <li> stream: get rows as stream
 *
 * </ul>
 *
 *
 * @author Arvid Halma
 * @version 4-9-2017 - 10:45
 */
public class Csv {
    private String encoding;
    private String delimiter;
    private boolean unquote;
    private boolean emptyAsNull;
    private boolean useHeader;
    private Function<String, String> columnNameNormalizer;
    //    private File inputFile;
    private InputStream inputStream;

    private int limit;
    private int skip;

    private boolean halt;
    private Predicate<Row> skipCondition;
    private Predicate<Row> haltCondition;
    private String commmentLineStart;

    private Splitter splitter;
    private Stats stats;
    private Map<String, Integer> colnameToIx;

    enum Evaluation {SUCCESS, SKIP, ERROR, HALT}


    public Csv() {
        encoding = "UTF-8";
        setDelimiter("\t");
        unquote = true;
        emptyAsNull = true;
        halt = false;
        commmentLineStart = "#";
        stats = new Stats();
    }

    public String getEncoding() {
        return encoding;
    }

    public Csv setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public Csv setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        this.splitter = Splitter.on(Pattern.compile(delimiter + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)"));
        return this;
    }

    public boolean isUnquote() {
        return unquote;
    }

    public Csv setUnquote(boolean unquote) {
        this.unquote = unquote;
        return this;
    }

    public boolean isEmptyAsNull() {
        return emptyAsNull;
    }

    public Csv setEmptyAsNull(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
        return this;
    }

    public boolean isUseHeader() {
        return useHeader;
    }

    public Csv setUseHeader(boolean useHeader) {
        this.useHeader = useHeader;
        return this;
    }

    public String getCommmentLineStart() {
        return commmentLineStart;
    }

    public Csv setCommmentLineStart(String commmentLineStart) {
        this.commmentLineStart = commmentLineStart;
        return this;
    }

    public Function<String, String> getColumnNameNormalizer() {
        return columnNameNormalizer;
    }

    public Csv setColumnNameNormalizer(Function<String, String> columnNameNormalizer) {
        this.columnNameNormalizer = columnNameNormalizer;
        return this;
    }

    public Csv format(String encoding, String delimiter, boolean unquote) {
        this.encoding = encoding;
        this.unquote = unquote;
        setDelimiter(delimiter);
        return this;
    }

    public Csv formatTsv() {
        return format("UTF-8", "\t", true);
    }

    public Csv formatCsv() {
        return format("UTF-8", ",", true);
    }

    public Csv formatExcelTxt() {
        return format("UTF-16", "\t", true);
    }

    public Csv formatExcelCsv() {
        return format("Windows-1252", ",", true);
    }


    public Csv setInputFile(File inputFile) throws FileNotFoundException {
        this.inputStream = new FileInputStream(inputFile);
        return this;
    }

    public Csv setInputFile(String inputFile) throws FileNotFoundException {
        return setInputFile(new File(inputFile));
    }

    public Csv setInputStream(InputStream inputStream){
        this.inputStream = inputStream;
        return this;
    }

    public Csv setInput(String input) throws IOException {
        this.inputStream = IOUtils.toInputStream(input, encoding);
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Csv setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getSkip() {
        return skip;
    }

    public Csv setSkip(int skip) {
        this.skip = skip;
        return this;
    }

    public Stats getStats() {
        return stats;
    }

    public void halt() {
        halt = true;
    }

    public Csv setHaltCondition(Predicate<Row> haltCondition) {
        this.haltCondition = haltCondition;
        return this;
    }

    public Csv setSkipCondition(Predicate<Row> skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }

    public List<String> readFirstValues(File path) throws IOException {
        return splitter.splitToList(readFirstLine(path));
    }

    private String readFirstLine(File path) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding))) {
            return br.readLine();
        }
    }

    public Stream<Row> stream() throws IOException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
    }

    public RowIterator iterator() throws IOException {
        return new RowIterator(new BufferedReader(new InputStreamReader(inputStream, encoding)));
    }

    public <T> Iterator<T> iterator(Function<Row, T> rowMapper) throws IOException {
        return new MappingIterator<>(
                new RowIterator(new BufferedReader(new InputStreamReader(inputStream, encoding))),
                rowMapper);
    }


    public void process(Consumer<Row> rowProcessor) throws IOException {
        for (RowIterator it = iterator(); it.hasNext(); ) {
            Row row = it.next();
            try {
                rowProcessor.accept(row);
                stats.success++;
            } catch (Exception e) {
                stats.error++;
                stats.lastError = e.getMessage();
            }
        }
    }

    public List<Row> getRows() throws IOException {
        List<Row> result = new ArrayList<>();
        for (RowIterator it = iterator(); it.hasNext(); ) {
            Row row = it.next();
            try {
                result.add(row);
                stats.success++;
            } catch (Exception e) {
                stats.error++;
                stats.lastError = e.getMessage();
            }
        }
        return result;
    }

    public void process(Function<Row, Evaluation> rowProcessor) throws IOException {
        for (RowIterator it = iterator(); it.hasNext(); ) {
            Row row = it.next();
            try {
                Evaluation eval = rowProcessor.apply(row);
                switch (eval) {
                    case SUCCESS:
                        stats.success++;
                        break;
                    case SKIP:
                        stats.skipped++;
                        break;
                    case ERROR:
                        stats.error++;
                        break;
                }
                if (eval == Evaluation.HALT) {
                    it.close();
                    break;
                }
            } catch (Exception e) {
                stats.error++;
                stats.lastError = e.getMessage();
            }
        }
    }

    public String toLine(List<String> vals){
        return toLine(vals, unquote, delimiter);
    }

    public static String toLine(List<String> vals, boolean quote, String delimiter){
        return vals.stream().map(v -> quote ? "\"" + v + "\"" : v).collect(Collectors.joining(delimiter, "", "\n"));
    }

    public void convert(File target, List<String> header, Function<Row, Row> converter) throws IOException {
        convert(target, unquote, delimiter, encoding, header, converter);
    }

    public void convert(File target, boolean quote, String delimiter, String encoding, List<String> header, Function<Row, Row> converter) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), encoding))) {
            if(header != null){
                bw.write(toLine(header, quote, delimiter));
            }
            for (RowIterator it = iterator(); it.hasNext(); ) {
                Row row = it.next();
                try {
                    Row newRow = converter.apply(row);
                    bw.write(toLine(newRow.data, quote, delimiter));
                    stats.success++;
                } catch (Exception e) {
                    stats.error++;
                    stats.lastError = e.getMessage();
                }
            }
        }
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
    private static String unquote(String s) {
        if (s != null && s.length() >= 2
                && ((s.startsWith("\"") && s.endsWith("\""))
                || (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }

    public class Row {
        int line;

        List<String> data;

        public Row(Object ... data) {
            this.data = new ArrayList<>(data.length);
            for (Object datum : data) {
                this.data.add(datum.toString());
            }
        }

        public Row(int line, List<String> data) {
            this.line = line;
            this.data = data;
        }

        public int getLine() {
            return line;
        }

        public List<String> getData() {
            return data;
        }

        public String getString(int i) {
            try {
                return data.get(i);
            } catch (Exception e) {
                throw new NoSuchElementException("Can't retrieve field [" + i + "] on line " + line + " with data: " + data);
            }
        }

        public String getString(String columnName) {
            try {
                if(columnNameNormalizer != null){
                    columnName = columnNameNormalizer.apply(columnName);
                }
                return data.get(colnameToIx.get(columnName));
            } catch (Exception e) {
                throw new NoSuchElementException("Can't retrieve field [" + columnName + "] on line " + line + " with data: " + data);
            }
        }

        public String excelGetString(String col) {
            try {
                return data.get(toIndex(col));
            } catch (Exception e) {
                throw new NoSuchElementException("Can't retrieve field [" + col + "] on line " + line + " with data: " + data);
            }
        }

        public long getLong(int i){
            String string = getString(i);
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a LONG number. See: field [" + i + "] on line " + line + " with data: " + data);
            }
        }

        public long getLong(String columnName){
            String string = getString(columnName);
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a LONG number. See: field [" + columnName + "] on line " + line + " with data: " + data);
            }
        }

        public long excelGetLong(String col){
            String string = excelGetString(col);
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a LONG number. See: field [" + col + "] on line " + line + " with data: " + data);
            }
        }

        public int getInteger(int i){
            String string = getString(i);
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a INTEGER number. See: field [" + i + "] on line " + line + " with data: " + data);
            }
        }

        public int getInteger(String columnName){
            String string = getString(columnName);
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a INTEGER number. See: field [" + columnName + "] on line " + line + " with data: " + data);
            }
        }

        public int excelGetInteger(String col){
            String string = excelGetString(col);
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a INTEGER number. See: field [" + col + "] on line " + line + " with data: " + data);
            }
        }

        public double getDouble(int i){
            String string = getString(i);
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a DOUBLE number. See: field [" + i + "] on line " + line + " with data: " + data);
            }
        }

        public double getDouble(String columnName){
            String string = getString(columnName);
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a DOUBLE number. See: field [" + columnName + "] on line " + line + " with data: " + data);
            }
        }

        public double excelGetDouble(String col){
            String string = excelGetString(col);
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new NoSuchElementException("Value '"+string+"' is not a DOUBLE number. See: field [" + col + "] on line " + line + " with data: " + data);
            }
        }

        public <T> T get(int i, Function<String, T> mapper){
            return mapper.apply(getString(i));
        }

        public <T> T get(String columnName, Function<String, T> mapper){
            return mapper.apply(getString(columnName));
        }

        public <T> T excelGet(String columnName, Function<String, T> mapper){
            return mapper.apply(excelGetString(columnName));
        }

        public int toIndex(String colName) {
            int number = 0;
            for (int i = 0; i < colName.length(); i++) {
                number = number * 26 + (colName.charAt(i) - ('A' - 1));
            }
            return number - 1;
        }

        public String toColName(int ix) {
            ix++;
            StringBuilder sb = new StringBuilder();
            while (ix-- > 0) {
                sb.append((char) ('A' + (ix % 26)));
                ix /= 26;
            }
            return sb.reverse().toString();
        }

        public Set<String> getColumns(){
            return colnameToIx.keySet();
        }

        @Override
        public String toString() {
            return String.join(delimiter, data);
        }

    }

    class Stats {
        int success; // successfully iterated and processed by the user
        int error;
        int skipped; // skipped by user
        int meta; // headers / comments
        int total; // total number of lines in the file

        String lastError;

        public Stats() {
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Stats{");
            sb.append("success=").append(success);
            sb.append(", error=").append(error);
            sb.append(", skipped=").append(skipped);
            sb.append(", meta=").append(meta);
            sb.append(", total=").append(total);
            sb.append(", lastError='").append(lastError).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    /** Generalized from: org.apache.commons.io.LineIterator */
    public class RowIterator implements Iterator<Row> {
        private final BufferedReader bufferedReader;
        private String cachedLine;
        protected Row row;
        int number = 0;

        public RowIterator(Reader reader) throws IllegalArgumentException, IOException {
            if(reader == null) {
                throw new IllegalArgumentException("Reader must not be null");
            } else {
                if(reader instanceof BufferedReader) {
                    bufferedReader = (BufferedReader)reader;
                } else {
                    bufferedReader = new BufferedReader(reader);
                }

            }

            // prepare column info
            if (useHeader) {
                // read first line
                List<String> firstRow = splitter.splitToList(bufferedReader.readLine());
                stats.meta++;
                stats.total++;
                colnameToIx = new LinkedHashMap<>();
                for (int i = 0; i < firstRow.size(); i++) {
                    String key = firstRow.get(i).toLowerCase();
                    if(columnNameNormalizer != null){
                        key = columnNameNormalizer.apply(key);
                    }
                    if (!colnameToIx.containsKey(key)) // don't overwrite earlier col names
                        colnameToIx.put(key, i);
                }

            }
        }

        public boolean hasNext() {
            if(halt) {
                return false;
            } else if(cachedLine != null) {
                return true;
            } else {
                try {
                    // find new valid line
                    String line;
                    do {
                        line = bufferedReader.readLine();
                        number++;
                        stats.total++;
                        if(line == null) {
                            halt = true;
                        }
                    } while(!halt && !isValidLine(line));

                    if(halt){
                        close();
                        return false;
                    }

                    return true;
                } catch (IOException e) {
                    close();
                    throw new IllegalStateException(e);
                }
            }
        }

        // updates cachedLine and row state
        protected boolean isValidLine(String line) {

            if (line.startsWith(commmentLineStart)) {
                stats.meta++;
                return false;
            }

            if (number <= skip) {
                return false;
            }

            if (limit > 0 && number - skip > limit) {
                halt = true;
                return false;
            }

            // parseYaml line
            List<String> data = new ArrayList<>(splitter.splitToList(line));
            for (int i = 0; i < data.size(); i++) {
                String val = data.get(i);
                String v = unquote ? unquote(val) : val;
                data.set(i, emptyAsNull && v.isEmpty() ? null : v);
            }

            Row tmpRow = new Row(number, data);
            if (haltCondition != null && !haltCondition.test(tmpRow)) {
                halt = true;
                return false;
            }

            try {
                if(skipCondition != null && skipCondition.test(tmpRow)){
                    skip++;
                    return false;
                }
            } catch (Exception e){
                skip++;
                return false;
            }

            // save line
            row = tmpRow;
            cachedLine = line;

            return true;
        }

        public Row next() {
            return nextLine();
        }

        public Row nextLine() {
            if(!hasNext()) {
                throw new NoSuchElementException("No more lines");
            } else {
                cachedLine = null;
                return row;
            }
        }

        public void close() {
            halt = true;
            try {
                bufferedReader.close();
            } catch (IOException ignored) { }
            cachedLine = null;
            row = null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove unsupported on RowIterator");
        }
    }


}

