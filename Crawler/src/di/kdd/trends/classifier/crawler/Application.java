package di.kdd.trends.classifier.crawler;

import di.kdd.trends.classifier.crawler.config.Location;
import di.kdd.trends.classifier.crawler.config.LocationLoader;
import di.kdd.trends.classifier.crawler.config.Token;
import di.kdd.trends.classifier.crawler.config.TokenLoader;

import java.util.ArrayList;

/**
 * Created by panossakkos on 2/13/14.
 */

public class Application {

    public static final String CONFIGS_FOLDER = "Configs/";
    public static final String PUBLIC_TOKENS_FOLDER = CONFIGS_FOLDER + "/Tokens/PublicTokens/";
    public static final String SECRET_TOKENS_FOLDER = CONFIGS_FOLDER + "/Tokens/SecretTokens/";
    public static final String DATA_FOLDER = "Data/";

    private static ArrayList<Crawler> crawlers = new ArrayList<Crawler>();

    public static void main(String[] args) throws Exception {

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        Application.addShutDownHook();

        Token token;
        Location location;

        LocationLoader locationLoader = new LocationLoader();
        TokenLoader tokenLoader = new TokenLoader();

        if (args.length == 0) {

            /* Light'em all */

            while ((location = locationLoader.getLocation()) != null &&
                            (token = tokenLoader.getToken()) != null) {
                crawlers.add(new Crawler(location, token));
            }
        }
        else {

            for (String locationName : args) {
                location = locationLoader.getLocation(locationName);

                if (location == null) {
                    System.err.println("No location " + locationName);
                    continue;
                }

                token = tokenLoader.getToken(location.getId());

                if (token == null) {
                    System.err.println("No token for " + location.getName());
                    continue;
                }

                crawlers.add(new Crawler(location, token));
            }
        }

        for (Crawler crawler : crawlers) {
            crawler.start();
        }

        while (true) {
            for (Crawler crawler : crawlers) {
                if (crawler.isCrawling()) {
                    crawler.join();
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
