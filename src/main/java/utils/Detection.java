package utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Detection implements Serializable {
    private Calendar datetime;
    private String city;
    private double value;
    private String country;
    private String continent;
    private int utc;
    private String weather;

    private Calendar getCalendar(String datetime) {
        Calendar cal = Calendar.getInstance();
        Date date = DateUtil.checkDate(datetime);
        //Correct datetime already checked in preprocess
        //It cannot be null!
        cal.setTime(date);
        return cal;
    }

    public Detection(String datetime, String value, String city) {
        this.datetime = getCalendar(datetime);
        this.city = city;
        try {
            this.value = Double.parseDouble(value);
        } catch (Exception e) {
            this.weather = value;
        }
    }

    public int getHour() {
        return this.datetime.get(Calendar.HOUR_OF_DAY);
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getUtc() {
        return utc;
    }

    public void setUtc(int utc) {
        this.utc = utc;
    }

    public int getMonth() {
        return this.datetime.getTime().getMonth()+1;
    }

    public int getYear() {
        return this.datetime.getTime().getYear()+1900;
    }

    public int getDay() {
        return this.datetime.getTime().getDate();
    }

    public void setDatetime(Calendar datetime) {
        this.datetime = datetime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}