/**
 * Created by panossakkos on 2/13/14.
 */

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

            this.locations.add(new Location(tokens[0], Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3])));
        }
    }

    public Location getLocation () {
        return this.locations.get(this.locationIndex++);
    }
}
