/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    private static TweetsCrawler crawler;

    public static void main(String[] args) throws Exception {

        Application.addShutDownHook();

        Application.crawler = new TweetsCrawler();
        crawler.start();

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
