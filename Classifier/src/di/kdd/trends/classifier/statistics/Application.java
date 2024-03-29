package di.kdd.trends.classifier.statistics;

/**
 * Created by panos on 2/25/14.
 */

public class Application {

    public static String DATA_FOLDER = "ProcessedData/";
    public static String TWEETS_FILE = "/tweets";

    public static void main (String []args) throws Exception {

        if (args.length < 2) {
            System.err.println("Processed tweets file and trends file are needed as arguments");
            return;
        }

        String location = args[0];
        String date = args[1];

        Statistics.load(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE);

    }
}