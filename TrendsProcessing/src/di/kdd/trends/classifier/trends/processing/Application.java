package di.kdd.trends.classifier.trends.processing;

/**
 * Created by panossakkos on 2/19/14.
 */

public class Application {

    private static String DATA_FOLDER = "Data/";

    public static void main(String []args) throws Exception {
        TrendsProcessor trendsProcessor = new TrendsProcessor();
        trendsProcessor.process(Application.DATA_FOLDER + "trends-18-02");

        return;
    }
}
