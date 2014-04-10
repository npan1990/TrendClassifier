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

    public static int TWEETS_THRESHOLD = 100;

    private static boolean cached = false;
    public static ArrayList<String> cachedTrends = new ArrayList<String>();

    private static TrendsProcessor trendsProcessor = new TrendsProcessor();
    private static ArrayList<ProcessedTweet> tweets = new ArrayList<ProcessedTweet>();
    private static ArrayList<String> trends = new ArrayList<String>();

    public static boolean printToConsole = true;

    public static void load(String tweetsFileName) throws Exception {
        Statistics.clear();
        Statistics.loadTweets(tweetsFileName);
    }

    public static void senseTrend (TrendVector trendVector) throws Exception {
        System.out.println("Trend: " + trendVector.getTrend());
        System.out.println("Length: " + trendVector.getTrend().length());
        Statistics.findDistinctWordsOfTrend(trendVector.getTrend());
        Statistics.computeAveragesPerTweetOfTrend(trendVector);
//        Statistics.trendsProcessor.dump(trendVector.getTrend());
    }

    public static TrendVector getTrendFeatures(String trend, TrendVector.TrendClass trendClass) throws Exception {
        TrendVector trendVector = new TrendVector();
        trendVector.setTrendClass(trendClass);
        trendVector.setTrend(trend);
        trendVector.setTrendLength(trend.length());

        Statistics.computeAveragesPerTweetOfTrend(trendVector);
        Statistics.findDistinctWordsOfTrend(trend);
//        Statistics.computeDateRangeFeatures(trendVector);

        if (printToConsole) {
            System.out.println("Trend: " + trend);
            System.out.println("Length: " + trend.length());
//            Statistics.trendsProcessor.dump(trend);
        }

        return trendVector;
    }

    public static TrendVector getTrendFeatures(String trend) throws Exception {
        TrendVector trendVector = new TrendVector();
        trendVector.setTrend(trend);
        trendVector.setTrendLength(trend.length());

        Statistics.computeAveragesPerTweetOfTrend(trendVector);
        Statistics.findDistinctWordsOfTrend(trend);

        if (printToConsole) {
            System.out.println("Trend: " + trend);
            System.out.println("Length: " + trend.length());
//            Statistics.trendsProcessor.dump(trend);
        }

        return trendVector;
    }

    public static void clear() {
        Statistics.tweets.clear();
        Statistics.trends.clear();
        Statistics.cachedTrends.clear();
        Statistics.cached = false;
        Statistics.trendsProcessor.clear();
    }

    private static void loadTweets(String tweetsFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(tweetsFileName));
        String line;

        while ((line = reader.readLine()) != null) {
            try {
                ProcessedTweet processedTweet = new ProcessedTweet(line);
                //            if (!Statistics.tweets.contains(processedTweet))
                Statistics.tweets.add(processedTweet);
            }
            catch (Exception e) {
                continue;
            }


        }
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

        if (printToConsole) {
            System.out.println("Number of distinct words for " + trend + ": " + distinctWords.size());
        }
    }

    private static void computeAveragesPerTweetOfTrend(TrendVector trendVector) {
        int tweetsWithTrend = 0;
        int relevantTweetsCount = 0;

        int tokenPopulation, urlPopulation, mentionsPopulation,
                hashTagsPopulation, urls, replies, rts, favsPopulation, mediasPopulation;

        tokenPopulation = urlPopulation = mentionsPopulation
                = hashTagsPopulation = urls = replies = rts = favsPopulation = mediasPopulation = 0;

        List<User> usersList = new ArrayList<User>();

        for (ProcessedTweet tweet : Statistics.tweets) {

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

                relevantTweetsCount++;

                // Keep unique users for this trend

                //User
                User user = tweet.getUser();
                if (!usersList.contains(user)) {
                    usersList.add(user);
                }
            }
        }

        int totalTweets = Statistics.tweets.size();

        // Go over users list and compute user-related features
        int uniqueUsers = usersList.size();
        double tweetsPerUser = (double)tweetsWithTrend / uniqueUsers;

        long usersListedPopulation, usersFollowersPopulation, usersFriendsPopulation,
                usersStatusesPopulation,  verifiedUsersPopulation;

        usersListedPopulation = usersFollowersPopulation = usersFriendsPopulation
                = usersStatusesPopulation =  verifiedUsersPopulation = 0;

        for (User user : usersList) {
            usersFollowersPopulation += user.getUserFollowersCount();
            usersFriendsPopulation += user.getUserFriendsCount();
            usersStatusesPopulation += user.getUserStatusesCount();
            if (user.isUserVerified()) {
                verifiedUsersPopulation++;
            }
        }

        trendVector.setTokensPerTweet((double) tokenPopulation / tweetsWithTrend);
        trendVector.setMentionsPerTweet((double) mentionsPopulation / tweetsWithTrend);
        trendVector.setHashTagsPerTweet((double) hashTagsPopulation / tweetsWithTrend);
        trendVector.setPercentageOfTweetsWithUrl((double) urls / tweetsWithTrend);
        trendVector.setRepliesPerTrend((double) replies / tweetsWithTrend);
        trendVector.setFavoritesPerTweet((double) favsPopulation / tweetsWithTrend);
        trendVector.setUrlsPerTweet((double) urlPopulation / tweetsWithTrend);
        trendVector.setMediasPerTweet((double) mediasPopulation / tweetsWithTrend);
        trendVector.setRetweetsPerTweet((double)rts / tweetsWithTrend);

        // Users features
        trendVector.setTweetsPerUser(tweetsPerUser);
        trendVector.setUniqueUsers(uniqueUsers);
        trendVector.setListedCountPerUser((double) usersListedPopulation / uniqueUsers);
        trendVector.setUserFollowersPerUser((double) usersFollowersPopulation / uniqueUsers);
        trendVector.setUserFriendsPerUser((double) usersFriendsPopulation / uniqueUsers);
        trendVector.setUserStatusesPerUser((double) usersStatusesPopulation / uniqueUsers);
        trendVector.setAvgVerifiedUsers((double) verifiedUsersPopulation / uniqueUsers);

        if (printToConsole) {
            System.out.println("Found in " + tweetsWithTrend + " out of " + Statistics.tweets.size() +  " tweets (" + (double) tweetsWithTrend / Statistics.tweets.size() + ")");
            System.out.println("Average tokens per tweet for " + trendVector.getTrend() + ": " + (double) tokenPopulation / tweetsWithTrend);
            System.out.println("Average urls per tweet for " + trendVector.getTrend() + ": " + (double) urlPopulation / tweetsWithTrend);
            System.out.println("Average mentions per tweet for " + trendVector.getTrend() + ": " + (double) mentionsPopulation / tweetsWithTrend);
            System.out.println("Average hash tags per tweet for " + trendVector.getTrend() + ": " + (double) hashTagsPopulation / tweetsWithTrend);
            System.out.println("Average favs per tweet for " + trendVector.getTrend() + ": " + (double) favsPopulation / tweetsWithTrend);
            System.out.println("Average media per tweet for " + trendVector.getTrend() + ": " + (double) mediasPopulation / tweetsWithTrend);
            System.out.println("Percentage of tweets with url: " +  (double)urls / tweetsWithTrend);
            System.out.println("Replies percentage:: " +  (double) replies / tweetsWithTrend);
            System.out.println("Retweets percentage of : " + (double) rts / tweetsWithTrend);
            System.out.println();
        }
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

    public static void listAll() {
        for (String trend : Statistics.trends) {
            System.out.println(trend);
        }
    }

    public static void filterTrends() {
        if (cached == false) {
            for (String trend : Statistics.trends) {
                int tweetsCount = 0;

                for (ProcessedTweet tweet : Statistics.tweets) {
                    if (Statistics.isRelevant(tweet, trend)) {
                        tweetsCount++;
                    }
                }

                if (tweetsCount >= Statistics.TWEETS_THRESHOLD) {
                    System.out.println(trend);
                    Statistics.cachedTrends.add(trend);
                }
            }
        }

        cached = true;
    }

    public static void list() {

        Statistics.filterTrends();

        for (String trend : Statistics.cachedTrends) {
            System.out.println(trend);
        }
    }

    public static ArrayList<String> getTrends() {
        return Statistics.trends;
    }

    public static void hashtags(int threshold) {

        HashMap<String, Integer> allHashtags = new HashMap<String, Integer>();

        // Iterate over all tweets from stream and get hashtags
        for (ProcessedTweet tweet : Statistics.tweets) {

            if (tweet.isFromSearch())
                continue;

            List<String> tweetHashtags = tweet.getHashTags();

            // Create a set with raw - lowercased - hashtags from tweet
            HashSet<String> tweetHashtagSet = new HashSet<String>();
            for (String hashtag : tweetHashtags) {
                String rawHashtag = hashtag.toLowerCase().trim();
                if (!tweetHashtagSet.contains(rawHashtag)) {
                    tweetHashtagSet.add(rawHashtag);
                }
            }

            for (String hashtag : tweetHashtagSet) {
                if (allHashtags.get(hashtag)==null){
                    allHashtags.put(hashtag, new Integer(1));
                }
                else {
                    Integer uptoNow = allHashtags.get(hashtag);
                    uptoNow++;
                    allHashtags.put(hashtag, uptoNow);
                }
            }
        }

        boolean ASC = true;
        Map<String, Integer> sortedMapAsc = sortByComparator(allHashtags, ASC);
        printMap(sortedMapAsc, threshold);


    }



    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void printMap(Map<String, Integer> map, int threshold) {
        int count = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > threshold) {
                System.out.println(entry.getKey() + ","+ entry.getValue());
                count++;
            }
        }

        System.out.println("Found " + count + " unique hashtags with more than " + threshold + " occurences");
    }
}
