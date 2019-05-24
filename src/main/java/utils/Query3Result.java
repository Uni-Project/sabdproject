package utils;

import java.io.Serializable;

public class Query3Result implements Serializable {
    private long id;

    private int position2016;
    private int position2017;
    private String country;
    private String city;
    private double value;

    public Query3Result(long id, int position2016, int position2017, String country, String city, double value) {
        this.id = id;
        this.position2016 = position2016;
        this.position2017 = position2017;
        this.country = country;
        this.city = city;
        this.value = value;
    }
}
