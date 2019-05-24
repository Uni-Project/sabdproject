package utils;

import java.io.Serializable;

public class Query2Result implements Serializable {
    private long id;

    private String country;
    private int year;
    private int month;

    private double max;
    private double min;
    private double stdev;
    private double mean;

    public Query2Result(long id, String country, int year, int month, double max, double min, double stdev, double mean) {
        this.id = id;
        this.country = country;
        this.year = year;
        this.month = month;
        this.max = max;
        this.min = min;
        this.stdev = stdev;
        this.mean = mean;
    }
}
