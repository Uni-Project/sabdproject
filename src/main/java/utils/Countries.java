package utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Countries {

    public static String getCountry(Double lat, Double lon) {
        int[] latIsraele = new int[] {29, 32, 34, 32};
        int[] lonIsraele = new int[] {35, 33, 34, 37};

        int[] latUsa = new int[] {45, 28, 20, 55};
        int[] lonUsa = new int[] {-135, -120, -155, -55};

        int[] latNorthAmerica = new int[] {90, 90, 78, 57, 15, 15, 1, 1, 51, 60, 60};
        int[] lonNorthAmerica = new int[] {-168, -10, -10, -37, -30, -75, -82, -105, -180, -180, -168};

        int[] latSouthAmerica = new int[] {1 ,  1 , 15,  15 ,-60, -60};
        int[] lonSouthAmerica = new int[] {-105 ,-82 , -75 ,-30, -30, -105};

        int[] latEurope = new int[] {90, 90, 42, 42, 40, 41, 41, 40,40 ,39 ,35 ,33 ,38 ,35 ,28 ,15 ,57, 78};
        int[] lonEurope = new int[] {-10 ,77 ,48 ,30 ,29 ,29 ,27 ,27 ,26 ,25 ,28 ,27 ,10 ,-10,-13, -30 ,-37 ,-10};

        int[] latAfrica = new int[] {15 , 28, 35, 38, 33  , 32, 30, 28, 11 ,12, -60, -60};
        int[] lonAfrica = new int[] {-30, -13 ,  -10, 10, 27, 35, 35 ,34 ,44, 52, 75 ,-30};

        int[] latAustralia = new int[] {-12, -11 ,-10 ,-30   , -52, -32};
        int[] lonAustralia = new int[] {110    ,  140 , 145 ,161 ,142 , 110};

        int[] latAsia1 = new int[] {90  , 42 ,42, 41 ,41 ,40, 40 , 40, 39 ,35, 33  , 31, 29, 27, 11 ,12, -60 ,-60 ,-32, -12 ,-10 ,33, 51  ,  60 , 90};
        int[] lonAsia1 = new int[] {77 ,49, 30 ,  29 ,29 ,27, 27, 26, 25 ,28 ,27 ,35 ,35, 34, 44 ,52,   75 , 110 , 110 ,  110 ,   140 ,   140 ,  167 ,180 ,180};
        int[] latAsia2 = new int[] {90   , 90   ,   60   ,   60};
        int[] lonAsia2 = new int[] {-180, -169, -169, -180};

        int[] latAntartide = new int[] {-60 ,-60 ,-90 ,-90};
        int[] lonAntartide = new int[] {-180, 180, 180, -180};

        //You have to maintain this order!!!
        Polygon isr = new Polygon(lonIsraele, latIsraele, 4);
        Polygon usa = new Polygon(lonUsa, latUsa, 4);
        Polygon nAmerica = new Polygon(lonNorthAmerica, latNorthAmerica, 11);
        Polygon sAmerica = new Polygon(lonSouthAmerica, latSouthAmerica, 6);
        Polygon eur = new Polygon(lonEurope, latEurope, 18);
        Polygon afr = new Polygon(lonAfrica, latAfrica, 12);
        Polygon aus = new Polygon(lonAustralia, latAustralia, 6);
        Polygon as1 = new Polygon(lonAsia1, latAsia1, 25);
        Polygon as2 = new Polygon(lonAsia2, latAsia2, 4);
        Polygon ant = new Polygon(lonAntartide, latAntartide, 4);

        List<Polygon> countries = Arrays.asList(isr, usa, nAmerica, sAmerica, eur, afr, aus, as1, as2, ant);
        List<String> countryNames = Arrays.asList("Israele", "Usa", "North America", "South America", "Europe", "Africa", "Australia", "Asia", "Asia", "Antartide");

        for (int i=0; i<countries.size(); i++) {
            if (countries.get(i).contains(lat, lon))
                return countryNames.get(i);
        }

        return ""; //Not happening...entire world covered

    }

    public static Integer getUTC(String country) {
        if (country.isEmpty())
            return 0;

        List<String> countryNames = Arrays.asList("Israele", "Usa", "North America", "South America", "Europe", "Africa", "Australia", "Asia", "Asia", "Antartide");
        Integer[] UTCs = {3, -7, -7, -4, 5, 2, 9, 6, 7, 0}; //This data is very approximate!

        for (int i=0; i<countryNames.size(); i++)
            if (countryNames.get(i).equals(country))
                return UTCs[i];

        return 0;
    }

}

