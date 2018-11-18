package org.c4i.chitchat.api.cmd;

import org.c4i.util.Csv;

import java.io.*;
import java.util.*;

/**
 * Generate unigue ids for each call and deduplicate.
 * For input data, see: geonames.org.
 * @author Arvid Halma
 */
public class CreateCountryDataSheet {

    public static void main(String[] args) throws IOException {
        boolean variants = false;


        final String inputFile = args[0];
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(new File(inputFile).getParent(), "COUNTRY.csv")), "UTF-8"))) {
            bw.write("name\tcode\tpopulation\n");

        new Csv()
                .setInputFile(inputFile)
//                .setSkipCondition(row -> (countryCode != null && !countryCode.equals(row.getString(8)) || (row.getString(14) == null || row.getInteger(14) < 50_000))) // cities with pop > 50_000
                .process(row -> {

                    String name = row.getString(4); // ISO-3166 2-letter country code, 2 characters
                    String code = row.getString(0); // ISO-3166 2-letter country code, 2 characters
                    String pop = row.getString(6);

                    try {
                        bw.write(name);
                        bw.write('\t');
                        bw.write(code);
                        bw.write('\t');
                        bw.write(pop);
                        bw.write('\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


        }
    }





}
