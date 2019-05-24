package utils;

import scala.Serializable;

import java.util.List;

public class Query1Result implements Serializable {
    private long id;

    private int year;
    private List<String> cities;

    public Query1Result(long id, int year, List<String> cities) {
        this.id = id;
        this.year = year;
        this.cities = cities;
    }
}
