package di.kdd.trends.classifier.processing;

import java.util.ArrayList;

/**
 * Created by panos on 3/11/14.
 */

public class TrendVector {

    public enum TrendClass { Meme, PlannedEvent, UnplannedEvent, General };

    private static String VALUE_SEPARATOR = ", ";
    private static int TREND_INDEX = 0;
    private static int CLASS_INDEX = 1;

    private String trend;
    private TrendClass trendClass;

    /* Dimensions of feature vector */

    private double relevantTweetsFromStream;
    private double tokensPerTweet;
    private double mentionsPerTweet;
    private double hashTagsPerTweet;
    private double tweetsWithUrl;
    private double tweetsWithReplies;
    private double tweetsWithRts;

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

    public double getRelevantTweetsFromStream () { return this.relevantTweetsFromStream; }

    public double getTokensPerTweet () { return this.tokensPerTweet; }

    public double getMentionsPerTweet () { return this.mentionsPerTweet; }

    public double getHashTagsPerTweet () { return this.hashTagsPerTweet; }

    public double getTweetsWithUrl () { return this.tweetsWithUrl; }

    private double getTweetsWithReplies () { return this.tweetsWithReplies; }

    private double getTweetsWithRts () { return this.tweetsWithRts; }

    public void setTrend (String trend) { this.trend = trend; }

    public void setRelevantTweetsFromStream (double relevantTweetsFromStream) { this.relevantTweetsFromStream  = relevantTweetsFromStream; }

    public void setTokensPerTweet (double tokensPerTweet) { this.tokensPerTweet = tokensPerTweet; }

    public void setMentionsPerTweet (double mentionsPerTweet) { this.mentionsPerTweet = mentionsPerTweet; }

    public void setHashTagsPerTweet (double hashTagsPerTweet) { this.hashTagsPerTweet = hashTagsPerTweet; }

    public void setTrendClass (TrendClass trendClass) { this.trendClass = trendClass; }

    public void setTweetsWithUrl (double tweetsWithUrl) { this.tweetsWithUrl = tweetsWithUrl; }

    public void setTweetsWithReplies (double tweetsWithReplies) { this.tweetsWithReplies = tweetsWithReplies; }

    public void setTweetsWithRts (double tweetsWithRts) { this.tweetsWithRts = tweetsWithRts; }

    public String toCsv () {
        String vectorCsv = this.trend + TrendVector.VALUE_SEPARATOR
                            + this.trendClass + TrendVector.VALUE_SEPARATOR
                            + this.relevantTweetsFromStream + TrendVector.VALUE_SEPARATOR
                            + this.tokensPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.mentionsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.hashTagsPerTweet + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithUrl + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithReplies + TrendVector.VALUE_SEPARATOR
                            + this.tweetsWithRts + TrendVector.VALUE_SEPARATOR;

        return vectorCsv;
    }
}
