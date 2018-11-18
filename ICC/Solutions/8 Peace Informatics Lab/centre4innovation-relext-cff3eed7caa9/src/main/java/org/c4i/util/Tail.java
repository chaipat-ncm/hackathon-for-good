package org.c4i.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Get last n lines of a file (a.k.a. Unix 'tail -n').
 * See: http://codereview.stackexchange.com/questions/79039/get-the-tail-of-a-file-the-last-10-lines
 * @author Arvid Halma
 */
public class Tail {

    private static final class RingBuffer {
        private final int limit;
        private final String[] data;
        private int counter = 0;

        public RingBuffer(int limit) {
            this.limit = limit;
            this.data = new String[limit];
        }

        public void collect(String line) {
            data[counter++ % limit] = line;
        }

        public List<String> contents() {
            return IntStream.range(counter < limit ? 0 : counter - limit, counter)
                    .mapToObj(index -> data[index % limit])
                    .collect(Collectors.toList());
        }

    }

    public static List<String> tailFile(final Path source, final int limit) throws IOException {

        try (Stream<String> stream = Files.lines(source)) {
            RingBuffer buffer = new RingBuffer(limit);
            stream.forEach(buffer::collect);

            return buffer.contents();
        }

    }

    public static String tailFileText(final Path source, final int limit) throws IOException {
        return tailFile(source, limit).stream().collect(Collectors.joining("\n"));
    }

    public static void main(String[] args) throws IOException {
        tailFile(Paths.get(args[0]), 10).forEach(System.out::println);
    }
}