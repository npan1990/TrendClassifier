import java.util.ArrayList;

/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    public static final String CONFIGS_FOLDER = "Configs/";
    public static final String PUBLIC_TOKENS_FOLDER = "PublicTokens/";
    public static final String SECRET_TOKENS_FOLDER = "SecretTokens/";
    public static final String DATA_FOLDER = "Data/";

    private static ArrayList<Crawler> crawlers = new ArrayList<Crawler>();

    public static void main(String[] args) throws Exception {

        Application.addShutDownHook();

        Location location;
        LocationLoader locationLoader = new LocationLoader();
        Token token;
        TokenLoader tokenLoader = new TokenLoader();

        while ((location = locationLoader.getLocation()) != null &&
                (token = tokenLoader.getToken()) != null) {
            crawlers.add(new Crawler(location, token));
        }

        for (Crawler crawler : crawlers) {
            crawler.start();
        }

        while (true) {

            for (Crawler crawler : crawlers) {
                if (crawler.isCrawling()) {
                    System.out.println(crawler.getCrawlerName() + " is crawling");
                }
            }
        }
    }

    private static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                for (Crawler crawler : crawlers) {
                    crawler.stopCrawling();
                }
            }
        });
    }
}
