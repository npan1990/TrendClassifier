package di.kdd.trends.classifier.crawler.config; /**
 * Created by panossakkos on 2/13/14.
 */

import di.kdd.trends.classifier.crawler.Application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LocationLoader {

    private static final String LOCATIONS = "LOCATIONS";

    private int locationIndex = 0;
    private ArrayList<Location> locations = new ArrayList<Location>();

    public LocationLoader() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Application.CONFIGS_FOLDER + LocationLoader.LOCATIONS));

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(" ");

            this.locations.add(new Location(tokens[0],
                    Double.parseDouble(tokens[1]),
                    Double.parseDouble(tokens[2]),
                    Double.parseDouble(tokens[3]),
                    Double.parseDouble(tokens[4]),
                    Double.parseDouble(tokens[5]),
                    Double.parseDouble(tokens[6]),
                    Double.parseDouble(tokens[7]),
                    Integer.parseInt(tokens[8])));
        }
    }

    public Location getLocation () {

        if (this.locationIndex == this.locations.size()) {
            return null;
        }

        return this.locations.get(this.locationIndex++);
    }

    public Location getLocation (String name) {
        for (Location location : this.locations) {
            if (location.getName().toLowerCase().equals(name.toLowerCase())) {
                return location;
            }
        }

        return null;
    }
}