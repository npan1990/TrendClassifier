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

    private double swlong, swlat, nelong, nelat;

    public Location (String name, double latitude, double longitude, double swlong,
                     double swlat, double nelong, double nelat,  double radius, int woeid) {
        this.id = Location.ID++;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.swlong = swlong;
        this.swlat = swlat;
        this.nelong = nelong;
        this.nelat = nelat;
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

    public double getSwlong() { return swlong; }

    public double getSwlat() { return swlat; }

    public double getNelong() { return nelong; }

    public double getNelat() { return nelat; }
}
