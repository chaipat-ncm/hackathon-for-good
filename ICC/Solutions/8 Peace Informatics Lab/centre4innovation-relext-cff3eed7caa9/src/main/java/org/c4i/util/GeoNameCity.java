package org.c4i.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTimeZone;

import java.util.Set;

/**
 * City info from http://www.geonames.org.
 *
 * geonameid         : integer id of record in geonames database
 * name              : name of geographical point (utf8) varchar(200)
 * asciiname         : name of geographical point in plain ascii characters, varchar(200)
 * alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
 * latitude          : latitude in decimal degrees (wgs84)
 * longitude         : longitude in decimal degrees (wgs84)
 * feature class     : see http://www.geonames.org/export/codes.html, char(1)
 * feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
 * country code      : ISO-3166 2-letter country code, 2 characters
 * cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
 * admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
 * admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 
 * admin3 code       : code for third level administrative division, varchar(20)
 * admin4 code       : code for fourth level administrative division, varchar(20)
 * population        : bigint (8 byte int) 
 * elevation         : in meters, integer
 * dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
 * timezone          : the timezone id (see file timeZone.txt) varchar(40)
 * modification date : date of last modification in yyyy-MM-dd format
 *
 * @author Arvid Halma
 * @version 25-8-17
 */
public class GeoNameCity {

    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Set<String> alternateNames;

    @JsonProperty
    private double latitude;
    @JsonProperty
    private double longitude;
    @JsonProperty
    private String countryCode;
    @JsonProperty
    private long population;
    @JsonProperty
    private int elevation;
    @JsonProperty
    private DateTimeZone timezone;

    public GeoNameCity() {
    }

    public int getId() {
        return id;
    }

    public GeoNameCity setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GeoNameCity setName(String name) {
        this.name = name;
        return this;
    }

    public Set<String> getAlternateNames() {
        return alternateNames;
    }

    public GeoNameCity setAlternateNames(Set<String> alternateNames) {
        this.alternateNames = alternateNames;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public GeoNameCity setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoNameCity setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public GeoNameCity setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public long getPopulation() {
        return population;
    }

    public GeoNameCity setPopulation(long population) {
        this.population = population;
        return this;
    }

    public int getElevation() {
        return elevation;
    }

    public GeoNameCity setElevation(int elevation) {
        this.elevation = elevation;
        return this;
    }

    public DateTimeZone getTimezone() {
        return timezone;
    }

    public GeoNameCity setTimezone(DateTimeZone timezone) {
        this.timezone = timezone;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoNameCity that = (GeoNameCity) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "GeoNameCity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alternateNames=" + alternateNames +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", countryCode='" + countryCode + '\'' +
                ", population=" + population +
                ", elevation=" + elevation +
                ", timezone=" + timezone +
                '}';
    }
}
