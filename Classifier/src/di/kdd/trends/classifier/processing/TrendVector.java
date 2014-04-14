package di.kdd.trends.classifier.processing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by panos on 3/11/14.
 */

public class TrendVector {




    public enum TrendClass { Meme, Event };


    private static String VALUE_SEPARATOR = ", ";
    private static int TREND_INDEX = 0;
    private static int CLASS_INDEX = 1;

    protected String trend;
    protected TrendClass trendClass;

    /* Dimensions of feature vector */

    protected int trendLength;
    protected double tokensPerTweet;
    protected double mentionsPerTweet;
    protected double hashTagsPerTweet;
    protected double tweetsWithUrl;
    protected double tweetsWithReplies;
    protected double retweetsPerTweet;
    protected double favoritesPerTweet;
    protected double urlsPerTweet;
    protected double mediasPerTweet;

    //User features
    protected double tweetsPerUser;
    protected int uniqueUsersCount;
    protected double userStatusesPerUser;
    protected double listedUsersPerUser;
    protected double userFollowersPerUser;
    protected double userFriendsPerUser;
    protected double avgVerifiedUsers;

    public static String getColumnNames() {
        String columns = "";
        boolean isFirst = true;

        for (Field field : TrendVector.class.getDeclaredFields()) {
            if (Modifier.isProtected(field.getModifiers())) {
                if (isFirst) {
                    columns += field.getName();
                    isFirst = false;
                }
                else {
                    columns += " " + field.getName();
                }
            }
        }

        return columns;
    }

    public TrendVector() { }

    public TrendVector(String csvString) {

        String []values = csvString.split(TrendVector.VALUE_SEPARATOR);

        this.trend = values[TrendVector.TREND_INDEX];
        this.trendClass = TrendClass.valueOf(values[TrendVector.CLASS_INDEX]);
    }

    public TrendVector(String trend, TrendClass trendClass) {
        this.trend = trend;
        this.trendClass = trendClass;
    }

    public String getTrend () { return this.trend; }

    public TrendClass getTrendClass () { return this.trendClass; }

    public int getTrendLength () { return this.trendLength; }

    public double getTokensPerTweet () { return this.tokensPerTweet; }

    public double getMentionsPerTweet () { return this.mentionsPerTweet; }

    public double getHashTagsPerTweet () { return this.hashTagsPerTweet; }

    public double getTweetsWithUrl () { return this.tweetsWithUrl; }

    private double getTweetsWithReplies () { return this.tweetsWithReplies; }

    public double getFavoritesPerTweet() { return favoritesPerTweet; }

    public double getRetweetsPerTweet() { return retweetsPerTweet; }

    public double getUrlsPerTweet() { return urlsPerTweet; }

    public double getMediasPerTweet() { return mediasPerTweet; }


    public void setTrend (String trend) { this.trend = trend; }

    public void setTrendClass (TrendClass trendClass) { this.trendClass = trendClass; }

    public void setTrendLength (int trendLength) { this.trendLength = trendLength; }

    public void setTokensPerTweet (double tokensPerTweet) { this.tokensPerTweet = tokensPerTweet; }

    public void setMentionsPerTweet (double mentionsPerTweet) { this.mentionsPerTweet = mentionsPerTweet; }

    public void setHashTagsPerTweet (double hashTagsPerTweet) { this.hashTagsPerTweet = hashTagsPerTweet; }

    public void setPercentageOfTweetsWithUrl(double tweetsWithUrl) { this.tweetsWithUrl = tweetsWithUrl; }

    public void setRepliesPerTrend(double tweetsWithReplies) { this.tweetsWithReplies = tweetsWithReplies; }

    public void setRetweetsPerTweet(double retweetsPerTweet) { this.retweetsPerTweet = retweetsPerTweet; }

    public void setFavoritesPerTweet(double favoritesPerTweet) { this.favoritesPerTweet = favoritesPerTweet; }

    public void setUrlsPerTweet(double urlsPerTweet) { this.urlsPerTweet = urlsPerTweet; }

    public void setMediasPerTweet(double mediasPerTweet) { this.mediasPerTweet = mediasPerTweet; }

    public void setTweetsPerUser(double tweetsPerUser) { this.tweetsPerUser = tweetsPerUser; }

    public void setUniqueUsers(int uniqueUsersCount ) { this.uniqueUsersCount = uniqueUsersCount; }

    public void setListedCountPerUser(double listedUsersPerUser) { this.listedUsersPerUser = listedUsersPerUser; }

    public void setUserStatusesPerUser(double userStatusesPerUser) { this.userStatusesPerUser = userStatusesPerUser; }

    public void setUserFriendsPerUser(double userFriendsPerUser) { this.userFriendsPerUser = userFriendsPerUser; }

    public void setUserFollowersPerUser(double userFollowersPerUser) { this.userFollowersPerUser = userFollowersPerUser; }

    public void setAvgVerifiedUsers(double avgVerifiedUsers) { this.avgVerifiedUsers = avgVerifiedUsers; }

    public String toCsv () {

        String vectorCsv =  this.trend + TrendVector.VALUE_SEPARATOR
                            + this.trendClass + TrendVector.VALUE_SEPARATOR
                            + this.trendLength + TrendVector.VALUE_SEPARATOR
                            + this.tokensPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.mentionsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.hashTagsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithUrl + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithReplies + TrendVector.VALUE_SEPARATOR
                            + this.retweetsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.favoritesPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.urlsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.mediasPerTweet + TrendVector.VALUE_SEPARATOR;

        // User features
        vectorCsv +=  this.tweetsPerUser + TrendVector.VALUE_SEPARATOR
                            + this.uniqueUsersCount + TrendVector.VALUE_SEPARATOR
                            + this.userStatusesPerUser + TrendVector.VALUE_SEPARATOR
                            + this.listedUsersPerUser + TrendVector.VALUE_SEPARATOR
                            + this.userFollowersPerUser + TrendVector.VALUE_SEPARATOR
                            + this.userFriendsPerUser + TrendVector.VALUE_SEPARATOR
                            + this.avgVerifiedUsers;

        return vectorCsv;
    }
}
