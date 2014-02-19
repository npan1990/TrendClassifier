package di.kdd.trends.classifier.crawler.config;

/**
 * Created by panossakkos on 2/13/14.
 */
public class Location {

    private static int ID = 0;

    private int id;
    private int woeid;
    private String name;
    private double latitude, longitude, radius;

    public Location (String name, double latitude, double longitude, double radius, int woeid) {
        this.id = Location.ID++;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.woeid = woeid;
    }

    public int getId() {
        return this.id;
    }

    public String getName () {
        return this.name;
    }

    public double getLatitude () {
        return this.latitude;
    }

    public double getLongitude () {
        return this.longitude;
    }

    public double getRadius () {
        return this.radius;
    }

    public int getWoeid () {
        return this.woeid;
    }
}