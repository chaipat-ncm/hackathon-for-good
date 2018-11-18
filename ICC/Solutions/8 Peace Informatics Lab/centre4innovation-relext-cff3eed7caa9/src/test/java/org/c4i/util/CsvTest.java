package org.c4i.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Arvid Halma
 * @version 8-9-2017 - 10:58
 */
public class CsvTest {
//    private static File inputFile = new File("data/test/dummy.csv");
    private static File inputFile = null;

    @Test
    public void indexTest() throws IOException {
        if(inputFile == null)
            return;

        new Csv()
                .formatCsv()
                .setInputFile(inputFile)
                .setSkip(1)
                .process( row -> {
                    System.out.println("row.getInteger(0) = " + row.getInteger(0));
                    System.out.println("row.getString(0) = " + row.getString(4));
                    return Csv.Evaluation.SUCCESS;
                        }
                );
    }

    @Test
    public void columnTest() throws IOException {
        if(inputFile == null)
            return;

        new Csv()
                .formatCsv()
                .setInputFile(new File("data/test/news.csv"))
                .setUseHeader(true)
                .process( row -> {
                            System.out.println("row.getInteger(id) = " + row.getInteger("id"));
                            System.out.println("row.getString(timestamp) = " + row.getString("timestamp"));
                            return Csv.Evaluation.SUCCESS;
                        }
                );
    }


    @Test
    public void limitTest() throws IOException {
        if(inputFile == null)
            return;

        new Csv()
                .formatCsv()
                .setInputFile(new File("data/test/dummy.csv"))
                .setUseHeader(true)
                .setLimit(3)
                .process( row -> {
                            System.out.println("row = " + row);
                            System.out.println("row.getInteger(id) = " + row.getInteger("id"));
                            System.out.println("row.getString(timestamp) = " + row.getString("timestamp"));
                            return Csv.Evaluation.SUCCESS;
                        }
                );
    }

    @Test
    public void streamTest() throws IOException {
        if(inputFile == null)
            return;

        new Csv()
                .formatCsv()
                .setInputFile(new File("data/test/dummy.csv"))
                .setUseHeader(true)
                .setLimit(3)
                .stream()
                .forEach(row -> {
                    System.out.println("row = " + row);
                    System.out.println("row.getInteger(id) = " + row.getInteger("id"));
                            System.out.println("row.getString(timestamp) = " + row.getString("timestamp"));
                        }
                );
    }




}
