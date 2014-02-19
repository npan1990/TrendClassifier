package di.kdd.trends.classifier.trends.processing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by panossakkos on 2/19/14.
 */

public class TrendsProcessor {

    private Hashtable<String, TrendValue> trends = new Hashtable<String, TrendValue>();

    public void process(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        while ((line = reader.readLine()) != null) {
            Date date = dateFormat.parse(line);

            for (int rank  = 1; rank <= 10; rank++) {
                line = reader.readLine().toLowerCase().replace("#", "");

                if (this.trends.containsKey(line)) {
                    this.trends.get(line).update(date, rank);
                }
                else {
                    this.trends.put(line, new TrendValue(date, rank));
                }
            }
        }
    }
}
