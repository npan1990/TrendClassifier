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

    private static Crawler crawler;

    public static void main(String[] args) throws Exception {

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        Application.addShutDownHook();

        Location location;

        LocationLoader locationLoader = new LocationLoader();

        if (args.length != 1){
            System.err.println("Run with only location as argument");
            return;
        }

        for (String locationName : args) {
            location = locationLoader.getLocation(locationName);

            if (location == null) {
                System.err.println("No location " + locationName);
                continue;
            }

            Application.crawler = new Crawler(location);
        }

        Application.crawler.start();

        while (true) {
                if (Application.crawler.isCrawling()) {
                    Application.crawler.join();
                }
        }
    }

    private static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Application.crawler.stopCrawling();
            }
        });
    }
}
