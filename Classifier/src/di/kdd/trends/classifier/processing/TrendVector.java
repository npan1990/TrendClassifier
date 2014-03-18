package di.kdd.trends.classifier.processing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by panos on 3/11/14.
 */

public class TrendVector {

    public enum TrendClass { Meme, PlannedEvent, UnplannedEvent, General };

    public static final int DAY_SLICES = 8;

    private static String VALUE_SEPARATOR = ", ";
    private static int TREND_INDEX = 0;
    private static int CLASS_INDEX = 1;

    protected String trend;
    protected TrendClass trendClass;

    /* Dimensions of feature vector */

    protected int trendLength;
    protected double relevantTweetsFromStream;
    protected double tokensPerTweet;
    protected double mentionsPerTweet;
    protected double hashTagsPerTweet;
    protected double tweetsWithUrl;
    protected double tweetsWithReplies;
    protected double tweetsWithRts;
    protected double retweetsPerTweet;
    protected double favoritesPerTweet;
    protected double urlsPerTweet;
    protected double mediasPerTweet;
    protected double averageRank;
    protected int mostDominantRank;
    protected int maximumRank;
    protected int duration;
    protected int durationOfLongestDateRange;
    protected boolean[] daySlices = new boolean[TrendVector.DAY_SLICES];

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

        for (int i = 1; i < TrendVector.DAY_SLICES - 1; i++) {
            columns += " " + i;
        }

        columns += " " + (TrendVector.DAY_SLICES - 1);

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

    public double getRelevantTweetsFromStream () { return this.relevantTweetsFromStream; }

    public double getTokensPerTweet () { return this.tokensPerTweet; }

    public double getMentionsPerTweet () { return this.mentionsPerTweet; }

    public double getHashTagsPerTweet () { return this.hashTagsPerTweet; }

    public double getTweetsWithUrl () { return this.tweetsWithUrl; }

    private double getTweetsWithReplies () { return this.tweetsWithReplies; }

    private double getTweetsWithRts () { return this.tweetsWithRts; }

    public double getFavoritesPerTweet() { return favoritesPerTweet; }

    public double getRetweetsPerTweet() { return retweetsPerTweet; }

    public double getUrlsPerTweet() { return urlsPerTweet; }

    public double getMediasPerTweet() { return mediasPerTweet; }

    public double getAverageRank() { return averageRank; }

    public int getMostDominantRank() { return mostDominantRank; }

    public int getMaximumRank() { return maximumRank; }

    public int getDuration() { return duration; }

    public int getDurationOfLongestDateRange() { return durationOfLongestDateRange; }

    public boolean[] getDaySlices() { return daySlices; }

    public void setTrend (String trend) { this.trend = trend; }

    public void setTrendClass (TrendClass trendClass) { this.trendClass = trendClass; }

    public void setTrendLength (int trendLength) { this.trendLength = trendLength; }

    public void setRelevantTweetsFromStream (double relevantTweetsFromStream) { this.relevantTweetsFromStream  = relevantTweetsFromStream; }

    public void setTokensPerTweet (double tokensPerTweet) { this.tokensPerTweet = tokensPerTweet; }

    public void setMentionsPerTweet (double mentionsPerTweet) { this.mentionsPerTweet = mentionsPerTweet; }

    public void setHashTagsPerTweet (double hashTagsPerTweet) { this.hashTagsPerTweet = hashTagsPerTweet; }

    public void setTweetsWithUrl (double tweetsWithUrl) { this.tweetsWithUrl = tweetsWithUrl; }

    public void setTweetsWithReplies (double tweetsWithReplies) { this.tweetsWithReplies = tweetsWithReplies; }

    public void setTweetsWithRts (double tweetsWithRts) { this.tweetsWithRts = tweetsWithRts; }

    public void setRetweetsPerTweet(double retweetsPerTweet) { this.retweetsPerTweet = retweetsPerTweet; }

    public void setFavoritesPerTweet(double favoritesPerTweet) { this.favoritesPerTweet = favoritesPerTweet; }


    public void setUrlsPerTweet(double urlsPerTweet) { this.urlsPerTweet = urlsPerTweet; }

    public void setMediasPerTweet(double mediasPerTweet) { this.mediasPerTweet = mediasPerTweet; }

    public void setAverageRank(double averageRank) {  this.averageRank = averageRank; }

    public void setMostDominantRank(int mostDominantRank) { this.mostDominantRank = mostDominantRank; }


    public void setMaximumRank(int maximumRank) { this.maximumRank = maximumRank; }

    public void setDuration(int duration) { this.duration = duration; }

    public void setDurationOfLongestDateRange(int durationOfLongestDateRange) { this.durationOfLongestDateRange = durationOfLongestDateRange; }

    public void setDaySlice(boolean []daySlices) {
        this.daySlices = daySlices;
    }


    public String toCsv () {

        String vectorCsv =  this.trend + TrendVector.VALUE_SEPARATOR
                            + this.trendClass + TrendVector.VALUE_SEPARATOR
                            + this.trendLength + TrendVector.VALUE_SEPARATOR
                            + this.relevantTweetsFromStream + TrendVector.VALUE_SEPARATOR
                            + this.tokensPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.mentionsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.hashTagsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithUrl + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithReplies + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithRts + TrendVector.VALUE_SEPARATOR
                            + this.retweetsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.favoritesPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.urlsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.mediasPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.averageRank + TrendVector.VALUE_SEPARATOR
                            + this.mostDominantRank + TrendVector.VALUE_SEPARATOR
                            + this.maximumRank + TrendVector.VALUE_SEPARATOR
                            + this.duration + TrendVector.VALUE_SEPARATOR
                            + this.durationOfLongestDateRange + TrendVector.VALUE_SEPARATOR;

        for (int i = 0; i < this.daySlices.length; i++) {
            if (i == this.daySlices.length - 1) {
                vectorCsv += this.daySlices[i];
            }
            else {
                vectorCsv += this.daySlices[i] + TrendVector.VALUE_SEPARATOR;
            }
        }

        return vectorCsv;
    }
}
