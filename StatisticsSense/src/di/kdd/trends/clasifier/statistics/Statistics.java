package di.kdd.trends.clasifier.statistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by panos on 2/25/14.
 */

public class Statistics {

    private static ArrayList<ProcessedTweet> tweets = new ArrayList<ProcessedTweet>();
    private static ArrayList<String> trends = new ArrayList<String>();

    public static void sense (String tweetsFileName, String trendsFileName) throws Exception {

        Statistics.loadTweets(tweetsFileName);
        Statistics.loadTrends(trendsFileName);

        Statistics.tweets.clear();
        Statistics.trends.clear();
    }

    private static void loadTweets(String tweetsFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(tweetsFileName));

        String line;

        while ((line = reader.readLine()) != null) {
            Statistics.tweets.add(new ProcessedTweet(line));
        }
    }

    private static void loadTrends(String trendsFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(trendsFileName));

        String line;

        while ((line = reader.readLine()) != null) {
            for (int i = 0; i < 10; i++) {
                String trend = reader.readLine().toLowerCase().replace("#", "");

                if (Statistics.trends.contains(trend) == false) {
                    Statistics.trends.add(trend);
                }
            }
        }
    }

}
