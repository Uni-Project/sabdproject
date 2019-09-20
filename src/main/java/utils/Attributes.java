package utils;

public class Attributes {
    private String country; //la country è country se abbiamo la country, altrimenti è continent
    private String continent;
    private String city;
    private Double lat;
    private Double lon;
    private int UTC;


    public Attributes(String city, String lat, String lon) {
        this.city = city;
        try {
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
            this.country = getCountryByCoord(this.lat, this.lon);
            this.UTC = calculateUTC(this.country);
        } catch (Exception ex) {
            this.lat = 0.0;
            this.lon = 0.0;
        }
    }


    private String getCountryByCoord(Double lon, Double lat) {
        return Countries.getCountry(lat, lon);
    }

    private int calculateUTC (String country) {
        return Countries.getUTC(country);
    }

    public int getUtc() {
        return this.UTC;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
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
