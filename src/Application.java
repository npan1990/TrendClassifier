/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    private static TweetsCrawler crawler;

    public static void main(String[] args) throws Exception {

        Application.crawler = new TweetsCrawler();
        crawler.start();
        crawler.stopCrawling();
    }
}
