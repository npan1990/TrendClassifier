package di.kdd.trends.classifier.processing;

/**
 * Created by panossakkos on 2/19/14.
 */

public class Application {

    private static String DATA_FOLDER = "Data/";
    private static String TREND_FILE = "/trends";
    private static String DUMP_FILE = "/trends.dump";

    public static void main(String []args) throws Exception {

        if (args.length == 0) {
            System.err.println("Location and date are needed as arguments.");
            return;
        }

        String location = args[0];
        String date = args[1];

        TrendsProcessor trendsProcessor = new TrendsProcessor();
        trendsProcessor.process(Application.DATA_FOLDER + location + "/" + date + Application.TREND_FILE);

        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                if (args[i].compareTo("dump") == 0) {
                    trendsProcessor.dumpTrends(Application.DATA_FOLDER + Application.DUMP_FILE);
                }
            }
        }
    }
}
