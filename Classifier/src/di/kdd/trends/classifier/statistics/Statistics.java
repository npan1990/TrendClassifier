package di.kdd.trends.classifier.statistics;

import di.kdd.trends.classifier.processing.TrendsProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by panos on 2/25/14.
 */

public class Statistics {

    private static TrendsProcessor trendsProcessor = new TrendsProcessor();
    private static ArrayList<ProcessedTweet> tweets = new ArrayList<ProcessedTweet>();
    private static ArrayList<String> trends = new ArrayList<String>();

    public static void load(String tweetsFileName, String trendsFileName) throws Exception {
        tweets.clear();
        trends.clear();
        Statistics.loadTweets(tweetsFileName);
        Statistics.loadTrends(trendsFileName);
    }

    public static void sense () throws Exception {
        Statistics.findDistinctWords();
        Statistics.computeAveragesPerTweet();
    }

    public static void senseTrend (String trend) throws Exception {
        System.out.println("Trend: " + trend);
        System.out.println("Length: " + trend.length());
        Statistics.findDistinctWordsOfTrend(trend);
        Statistics.computeAveragesPerTweetOfTrend(trend);
        Statistics.trendsProcessor.dump(trend);
    }

    public static void clear() {
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
        trendsProcessor.process(trendsFileName);
        Statistics.trends = trendsProcessor.getTrends();
    }

    private static void findDistinctWords() {
        ArrayList<String> distinctWords = new ArrayList<String>();

        for (ProcessedTweet tweet : Statistics.tweets) {
            for (String word : tweet.getTokens()) {
                if (distinctWords.contains(word) == false) {
                    distinctWords.add(word);
                }
            }
        }

        System.out.println("Number of distinct words: " + distinctWords.size());
    }

    private static void findDistinctWordsOfTrend(String trend) {
        ArrayList<String> distinctWords = new ArrayList<String>();

        for (ProcessedTweet tweet : Statistics.tweets) {
            if (Statistics.isRelevant(tweet, trend)) {
                for (String word : tweet.getTokens()) {
                    if (distinctWords.contains(word) == false) {
                        distinctWords.add(word);
                    }
                }

                for (String word : tweet.getHashTags()) {
                    if (distinctWords.contains(word) == false) {
                        distinctWords.add(word);
                    }
                }

            }
        }

        System.out.println("Number of distinct words for " + trend + ": " + distinctWords.size());
    }

    private static void computeAveragesPerTweet() {
        int tokenPopulation, urlPopulation, repliesPopulation, hashTagsPopulation, rts;

        tokenPopulation = urlPopulation = repliesPopulation = hashTagsPopulation = rts = 0;

        for (ProcessedTweet tweet : Statistics.tweets) {
            tokenPopulation += tweet.getTokens().size();
            urlPopulation += tweet.getUrls().size();
            repliesPopulation += tweet.getMentions().size();
            hashTagsPopulation += tweet.getHashTags().size();

            if (tweet.getIsRetweet()) {
                rts++;
            }
        }

        System.out.println("Average tokens per tweet: " + (double) tokenPopulation / Statistics.tweets.size());
        System.out.println("Average urls per tweet: " + (double) urlPopulation / Statistics.tweets.size());
        System.out.println("Average replies per tweet: " + (double) repliesPopulation / Statistics.tweets.size());
        System.out.println("Average hash tags per tweet: " + (double) hashTagsPopulation / Statistics.tweets.size());
        System.out.println("Percentage of tweets that were RTs: " +  rts * (double) 100 / Statistics.tweets.size());
        System.out.println();
    }

    private static void computeAveragesPerTweetOfTrend(String trend) {
        int tweetsWithTrend = 0;
        int tokenPopulation, urlPopulation, repliesPopulation, hashTagsPopulation, rts;

        tokenPopulation = urlPopulation = repliesPopulation = hashTagsPopulation = rts = 0;

        for (ProcessedTweet tweet : Statistics.tweets) {
            if (Statistics.isRelevant(tweet, trend)) {
                tokenPopulation += tweet.getTokens().size();
                urlPopulation += tweet.getUrls().size();
                repliesPopulation += tweet.getMentions().size();
                hashTagsPopulation += tweet.getHashTags().size();

                if (tweet.getIsRetweet()) {
                    rts++;
                }

                tweetsWithTrend++;
            }
        }

        System.out.println("Found in " + tweetsWithTrend + " out of " + Statistics.tweets.size() +  " tweets (" + (double) tweetsWithTrend / Statistics.tweets.size() + ")");
        System.out.println("Average tokens per tweet for " + trend + ": " + (double) tokenPopulation / tweetsWithTrend);
        System.out.println("Average urls per tweet for " + trend + ": " + (double) urlPopulation / tweetsWithTrend);
        System.out.println("Average replies per tweet for " + trend + ": " + (double) repliesPopulation / tweetsWithTrend);
        System.out.println("Average hash tags per tweet for " + trend + ": " + (double) hashTagsPopulation / tweetsWithTrend);
        System.out.println("Percentage of tweets that were RTs: " +  rts * (double) 100 / tweetsWithTrend);
        System.out.println();
    }

    private static boolean isRelevant(ProcessedTweet tweet, String trend) {

        ArrayList<String> searchSpace = new ArrayList<String>();

        searchSpace.addAll(tweet.getTokens());
        searchSpace.addAll(tweet.getHashTags());

        if (searchSpace.contains(trend)) {
            return true;
        }

        /* dani-alves */

        if (searchSpace.contains(trend.replace(" ", "_"))) {
            return true;
        }

        /* dani_alves */

        if (searchSpace.contains(trend.replace(" ", "-"))) {
            trend.replace(" ", "-");
            return true;
        }

        /* [...,dani,alves,...] */

        String []trendTokens = trend.split(" ");

        if (trendTokens.length > 0) {

            for (String subToken : trendTokens) {
                if (searchSpace.contains(subToken)) {
                    return true;
                }
            }

            for (int i = 0; i < tweet.getTokens().size(); i++) {

                boolean theyMatch = true;

                if (tweet.getTokens().get(i).compareTo(trendTokens[0]) == 0) {

                    i++;
                    for (int j = 1; i < tweet.getTokens().size() && j < trendTokens.length; i++, j++) {
                        if (tweet.getTokens().get(i).compareTo(trendTokens[j]) != 0) {
                            theyMatch = false;
                            break;
                        }
                    }

                    if (theyMatch) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public static void list() {
        for (String trend : Statistics.trends) {
            System.out.println(trend);
        }
    }
}
