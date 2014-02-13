/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    public static final String CONFIGS_FOLDER = "Configs/";
    public static final String SECRETS_FOLDER = "Secrets/";

    private static TweetsCrawler crawler;

    public static void main(String[] args) throws Exception {

        Application.addShutDownHook();

        LocationLoader locationLoader = new LocationLoader();

        Application.crawler = new TweetsCrawler(locationLoader.getLocation());
        Application.crawler.start();

        while (Application.crawler.isCrawling()) {

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
                Application.crawler.stopCrawling();
                System.out.println("Stopped");
            }
        });
    }
}
