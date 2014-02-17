/**
 * Created by panossakkos on 2/13/14.
 */
public class Location {

    private String name;
    private double latitude, longitude, radius;

    public Location (String name, double latitude, double longitude, double radius) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
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
}
