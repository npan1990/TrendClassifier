package di.kdd.trends.clasifier.statistics;

/**
 * Created by panos on 2/25/14.
 */
public class Application {

    private static String DATA_FOLDER = "Data/";
    private static String TRENDS_FILE = "/trends";
    private static String TWEETS_FILE = "/tweets.tok";

    public static void main (String []args) throws Exception {

        if (args.length < 2) {
            System.err.println("Processed tweets file and trends file are needed as arguments");
            return;
        }

        String location = args[0];
        String date = args[1];

        Statistics.sense(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE,
                        Application.DATA_FOLDER + location + "/" + date + Application.TRENDS_FILE);
    }
}