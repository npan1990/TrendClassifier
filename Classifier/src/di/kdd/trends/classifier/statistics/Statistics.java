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
            for (String word : tweet.getTokens()) {

                if (tweet.getTokens().contains(trend) || tweet.getHashTags().contains(trend)) {
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
            repliesPopulation += tweet.getReplies().size();
            hashTagsPopulation += tweet.getHashTags().size();

            if (tweet.getIsRwetweet()) {
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
            if (tweet.getTokens().contains(trend) || tweet.getHashTags().contains(trend)) {
                tokenPopulation += tweet.getTokens().size();
                urlPopulation += tweet.getUrls().size();
                repliesPopulation += tweet.getReplies().size();
                hashTagsPopulation += tweet.getHashTags().size();

                if (tweet.getIsRwetweet()) {
                    rts++;
                }

                tweetsWithTrend++;
            }
        }

        System.out.println("Found in " + tweetsWithTrend + " tweets");
        System.out.println("Average tokens per tweet for " + trend + ": " + (double) tokenPopulation / tweetsWithTrend);
        System.out.println("Average urls per tweet for " + trend + ": " + (double) urlPopulation / tweetsWithTrend);
        System.out.println("Average replies per tweet for " + trend + ": " + (double) repliesPopulation / tweetsWithTrend);
        System.out.println("Average hash tags per tweet for " + trend + ": " + (double) hashTagsPopulation / tweetsWithTrend);
        System.out.println("Percentage of tweets that were RTs: " +  rts * (double) 100 / tweetsWithTrend);
        System.out.println();
    }

    public static void list() {
        for (String trend : Statistics.trends) {
            System.out.println(trend);
        }
    }
}
