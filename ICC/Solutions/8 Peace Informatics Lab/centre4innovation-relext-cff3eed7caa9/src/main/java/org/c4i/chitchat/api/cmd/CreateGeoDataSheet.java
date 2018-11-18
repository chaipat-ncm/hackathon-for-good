package org.c4i.chitchat.api.cmd;

import org.c4i.util.Csv;

import java.io.*;
import java.util.*;

/**
 * Generate unigue ids for each call and deduplicate.
 * For input data, see: geonames.org.
 * @author Arvid Halma
 */
public class CreateGeoDataSheet {

    public static void main(String[] args) throws IOException {
        boolean variants = false;


        final String inputFile = args[0];
        final String countryCode = args.length > 1 ? args[1] : null;

        Map<String, City> cities = new LinkedHashMap<>();
        new Csv()
                .setInputFile(inputFile)
//                .setSkipCondition(row -> (countryCode != null && !countryCode.equals(row.getString(8)) || (row.getString(14) == null || row.getInteger(14) < 50_000))) // cities with pop > 50_000
                .setSkipCondition(row -> (countryCode != null && !countryCode.equals(row.getString(8))))
                .process(row -> {
                    String name = row.getString(1);
                    String[] names = row.getString(3).split(",");
                    List<String> allNames = new ArrayList<>();
                    allNames.add(name);
                    if(variants) {
                        allNames.addAll(Arrays.asList(names));
                    }
                    String country = row.getString(8); // ISO-3166 2-letter country code, 2 characters
                    String lat = row.getString(4);
                    String lng = row.getString(5);
                    String pop = row.getString(14);

                    // when city names clash, pick the largest
                    for (String altname : allNames) {
                        if(cities.containsKey(altname)) {
                            City existing = cities.get(altname);
                            if(Integer.parseInt(pop) > existing.pop){
                                cities.put(altname, new City(altname, name, country, lat, lng, pop));
                            }
                        } else {
                            cities.put(altname, new City(altname, name, country, lat, lng, pop));
                        }
                    }
                });

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(new File(inputFile).getParent(), countryCode == null ? "CITY.csv" : "CITY"+countryCode+".csv")), "UTF-8"))) {
            bw.write("name\tnormalname\tcountry\tlatitude\tlongitude\tpopulation\n");

                        for (City city : cities.values()) {
                                bw.write(city.name);
                                bw.write('\t');
                                bw.write(city.normalname);
                                bw.write('\t');
                                bw.write(city.country);
                                bw.write('\t');
                                bw.write(city.lat);
                                bw.write('\t');
                                bw.write(city.lon);
                                bw.write('\t');
                                bw.write(""+city.pop);
                                bw.write('\n');
                        }

        }
    }

    /**
     * Data entry for a city.
     */
    public static class City implements Comparable<City>{
        String name;
        String normalname;
        String country;
        String lat, lon;
        int pop;

        public City(String name, String normalname, String country, String lat, String lon, String pop) {
            this.name = name;
            this.normalname = normalname;
            this.country = country;
            this.lat = lat;
            this.lon = lon;
            this.pop = Integer.parseInt(pop);
        }

        @Override
        public int compareTo(City o) {
            return Integer.compare(pop, o.pop);
        }
    }



}
