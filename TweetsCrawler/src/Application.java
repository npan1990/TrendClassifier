import java.util.ArrayList;

/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    public static final String CONFIGS_FOLDER = "Configs/";
    public static final String PUBLIC_TOKENS_FOLDER = "PublicTokens/";
    public static final String SECRET_TOKENS_FOLDER = "SecretTokens/";
    public static final String TWEETS_FOLDER = "Tweets/";

    private static ArrayList<TweetsCrawler> crawlers = new ArrayList<TweetsCrawler>();

    public static void main(String[] args) throws Exception {

        Application.addShutDownHook();

        Location location;
        LocationLoader locationLoader = new LocationLoader();
        TokenLoader tokenLoader = new TokenLoader();

        while ((location = locationLoader.getLocation()) != null) {
            crawlers.add(new TweetsCrawler(location));
        }

        for (TweetsCrawler crawler : crawlers) {
            crawler.start();
        }

        while (true) {

            for (TweetsCrawler crawler : crawlers) {
                if (crawler.isCrawling()) {
                    System.out.println(crawler.getCrawlerName() + " is crawling");
                }
            }

            Thread.sleep(15 * 60 * 1000);
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
