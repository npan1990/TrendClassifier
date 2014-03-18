package di.kdd.trends.classifier.statistics;

import di.kdd.trends.classifier.processing.TrendVector;
import di.kdd.trends.classifier.processing.TrendsProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Created by panos on 2/25/14.
 */

public class Statistics {

    private static TrendsProcessor trendsProcessor = new TrendsProcessor();
    private static ArrayList<ProcessedTweet> tweets = new ArrayList<ProcessedTweet>();
    private static ArrayList<String> trends = new ArrayList<String>();

    public static void load(String tweetsFileName, String trendsFileName) throws Exception {
        Statistics.clear();
        Statistics.loadTweets(tweetsFileName);
        Statistics.loadTrends(trendsFileName);
    }

    public static void sense () throws Exception {
        Statistics.findDistinctWords();
        Statistics.computeAveragesPerTweet();
    }

    public static void senseTrend (TrendVector trendVector) throws Exception {
        System.out.println("Trend: " + trendVector.getTrend());
        System.out.println("Length: " + trendVector.getTrend().length());
        Statistics.findDistinctWordsOfTrend(trendVector.getTrend());
        Statistics.computeAveragesPerTweetOfTrend(trendVector);
        Statistics.computeDateRangeFeatures(trendVector);
        Statistics.trendsProcessor.dump(trendVector.getTrend());
    }

    public static TrendVector getTrendFeatures(String trend) throws Exception {
        TrendVector trendVector = new TrendVector();
        trendVector.setTrend(trend);

        System.out.println("Trend: " + trend);
        System.out.println("Length: " + trend.length());

        trendVector.setTrendLength(trend.length());

        Statistics.computeAveragesPerTweetOfTrend(trendVector);
        Statistics.findDistinctWordsOfTrend(trend);
        Statistics.computeDateRangeFeatures(trendVector);

        Statistics.trendsProcessor.dump(trend);

        return trendVector;
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

            if (tweet.isFromSearch() == false) {
                for (String word : tweet.getTokens()) {
                    if (distinctWords.contains(word) == false) {
                        distinctWords.add(word);
                    }
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
        int tokenPopulation, urlPopulation, repliesPopulation, hashTagsPopulation, urls, rts;

        tokenPopulation = urlPopulation = repliesPopulation = hashTagsPopulation = urls = rts = 0;

        for (ProcessedTweet tweet : Statistics.tweets) {
            if (tweet.isFromSearch() == false) {
                tokenPopulation += tweet.getTokens().size();
                urlPopulation += tweet.getUrlsCount();
                repliesPopulation += tweet.getMentions().size();
                hashTagsPopulation += tweet.getHashTags().size();

                if (tweet.getUrlsCount() > 0) {
                    urls++;
                }

                if (tweet.isRetweet()) {
                    rts++;
                }
            }
        }

        System.out.println("Average tokens per tweet: " + (double) tokenPopulation / Statistics.tweets.size());
        System.out.println("Average urls per tweet: " + (double) urlPopulation / Statistics.tweets.size());
        System.out.println("Average replies per tweet: " + (double) repliesPopulation / Statistics.tweets.size());
        System.out.println("Average hash tags per tweet: " + (double) hashTagsPopulation / Statistics.tweets.size());
        System.out.println("Percentage of tweets that had url: " +  urls * (double) 100 / Statistics.tweets.size());
        System.out.println("Percentage of tweets that were RTs: " +  rts * (double) 100 / Statistics.tweets.size());
        System.out.println();
    }

    private static void computeAveragesPerTweetOfTrend(TrendVector trendVector) {
        int tweetsWithTrend = 0;
        int tweetsFromStream = 0;
        int relevantTweetsFromStream = 0;

        int tokenPopulation, urlPopulation, mentionsPopulation,
                hashTagsPopulation, urls, replies, rts, favsPopulation, mediasPopulation;

        tokenPopulation = urlPopulation = mentionsPopulation
                = hashTagsPopulation = urls = replies = rts = favsPopulation = mediasPopulation =0;

        for (ProcessedTweet tweet : Statistics.tweets) {

            if (!tweet.isFromSearch()) {
                tweetsFromStream++;
            }

            if (Statistics.isRelevant(tweet, trendVector.getTrend())) {

                tweetsWithTrend++;

                tokenPopulation += tweet.getTokens().size();
                urlPopulation += tweet.getUrlsCount();
                mentionsPopulation += tweet.getMentions().size();
                hashTagsPopulation += tweet.getHashTags().size();
                mediasPopulation += tweet.getMediaCount();
                favsPopulation += tweet.getFavoriteCount();

                if (tweet.getUrlsCount() > 0) {
                    urls++;
                }

                if (tweet.isReply()) {
                    replies++;
                }

                // If this tweet is a retweet, add 1 to rts population
                if (tweet.isRetweet()) {
                    rts++;
                } else {
                    rts+= tweet.getRetweetCount();
                }

                if (!tweet.isFromSearch()) {
                    relevantTweetsFromStream++;
                }


            }
        }

        trendVector.setRelevantTweetsFromStream((double) relevantTweetsFromStream / tweetsFromStream);
        trendVector.setTokensPerTweet((double) tokenPopulation / tweetsWithTrend);
        trendVector.setMentionsPerTweet((double) mentionsPopulation / tweetsWithTrend);
        trendVector.setHashTagsPerTweet((double) hashTagsPopulation / tweetsWithTrend);
        trendVector.setTweetsWithUrl((double) urls / tweetsWithTrend);
        trendVector.setTweetsWithReplies((double) replies / tweetsWithTrend);
        trendVector.setTweetsWithRts((double) rts / tweetsWithTrend);

        trendVector.setFavoritesPerTweet((double) favsPopulation / tweetsWithTrend);
        trendVector.setUrlsPerTweet((double) urlPopulation / tweetsWithTrend);
        trendVector.setMediasPerTweet((double) mediasPopulation / tweetsWithTrend);

        System.out.println("Found in " + tweetsWithTrend + " out of " + Statistics.tweets.size() +  " tweets (" + (double) tweetsWithTrend / Statistics.tweets.size() + ")");
        System.out.println("Found in " + relevantTweetsFromStream + " out of " + tweetsFromStream +  " tweets from stream (" + (double) relevantTweetsFromStream / tweetsFromStream + ")");
        System.out.println("Average tokens per tweet for " + trendVector.getTrend() + ": " + (double) tokenPopulation / tweetsWithTrend);
        System.out.println("Average urls per tweet for " + trendVector.getTrend() + ": " + (double) urlPopulation / tweetsWithTrend);
        System.out.println("Average mentions per tweet for " + trendVector.getTrend() + ": " + (double) mentionsPopulation / tweetsWithTrend);
        System.out.println("Average hash tags per tweet for " + trendVector.getTrend() + ": " + (double) hashTagsPopulation / tweetsWithTrend);
        System.out.println("Average favs per tweet for " + trendVector.getTrend() + ": " + (double) favsPopulation / tweetsWithTrend);
        System.out.println("Average media per tweet for " + trendVector.getTrend() + ": " + (double) mediasPopulation / tweetsWithTrend);

        System.out.println("Percentage of tweets that had url: " +  urls * (double) 100 / tweetsWithTrend);
        System.out.println("Percentage of tweets that were replies: " +  replies * (double) 100 / tweetsWithTrend);
        System.out.println("Percentage of tweets that were RTs: " +  rts * (double) 100 / tweetsWithTrend);
        System.out.println();
    }

    private static void computeDateRangeFeatures(TrendVector trendVector) throws Exception {
        trendVector.setMaximumRank(Statistics.trendsProcessor.getMaxRank(trendVector.getTrend()));
        System.out.println("Maximum rank: " + trendVector.getMaximumRank());

        trendVector.setDuration(Statistics.trendsProcessor.getDuration(trendVector.getTrend()));
        System.out.println("Duration : " + trendVector.getDuration());

        trendVector.setDurationOfLongestDateRange(Statistics.trendsProcessor.getDurationOfLongestRange(trendVector.getTrend()));
        System.out.println("Duration of longest range: " + trendVector.getDurationOfLongestDateRange());

        trendVector.setAverageRank(Statistics.trendsProcessor.getAverageRank(trendVector.getTrend()));
        System.out.println("Average rank: " + trendVector.getAverageRank());

        trendVector.setMostDominantRank(Statistics.trendsProcessor.getMostDominantRank(trendVector.getTrend()));
        System.out.println("Most dominant rank: " + trendVector.getMostDominantRank());

        trendVector.setDaySlice(Statistics.trendsProcessor.getAppearanceSlices(trendVector.getTrend()));

        System.out.println("Appearances in day slices:");
        for (boolean appearance : trendVector.getDaySlices()) {
            System.out.print(appearance + " ");
        }
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

    public static ArrayList<String> getTrends() {
        return Statistics.trends;
    }
}
