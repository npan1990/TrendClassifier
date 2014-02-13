import java.util.ArrayList;

/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    public static final String CONFIGS_FOLDER = "Configs/";
    public static final String SECRETS_FOLDER = "Secrets/";
    public static final String TWEETS_FOLDER = "Tweets/";

    private static ArrayList<TweetsCrawler> crawlers = new ArrayList<TweetsCrawler>();

    public static void main(String[] args) throws Exception {

        Application.addShutDownHook();

        Location location;
        LocationLoader locationLoader = new LocationLoader();

        while ((location = locationLoader.getLocation()) != null) {
            crawlers.add(new TweetsCrawler(location));
        }

        for (TweetsCrawler crawler : crawlers) {
            crawler.start();
        }

        while (true) {

            System.out.println("Crawling");
            Thread.sleep(10000);

        }
    }

    private static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                for (TweetsCrawler crawler : crawlers) {
                    crawler.stopCrawling();
                }
            }
        });
    }
}
