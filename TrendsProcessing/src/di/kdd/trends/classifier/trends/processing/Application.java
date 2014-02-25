package di.kdd.trends.classifier.trends.processing;

/**
 * Created by panossakkos on 2/19/14.
 */

public class Application {

    private static String TREND_FILE = "trends";
    private static String DATA_FOLDER = "Data/";

    public static void main(String []args) throws Exception {

        if (args.length == 0) {
            System.err.println("Location and date are needed as arguments.");
            return;
        }

        String location = args[0];
        String date = args[1];

        TrendsProcessor trendsProcessor = new TrendsProcessor();
        trendsProcessor.process(Application.DATA_FOLDER + location + "/" + date + "/" + Application.TREND_FILE);

    }
}
