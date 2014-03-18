package di.kdd.trends.classifier.processing;

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

    private int trendLength;
    private double relevantTweetsFromStream;
    private double tokensPerTweet;
    private double mentionsPerTweet;
    private double hashTagsPerTweet;
    private double tweetsWithUrl;
    private double tweetsWithReplies;
    private double tweetsWithRts;
    private double retweetsPerTweet;
    private double favoritesPerTweet;
    private double symbolsPerTweet;
    private double urlsPerTweet;
    private double mediasPerTweet;

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

    public double getSymbolsPerTweet() { return symbolsPerTweet; }

    public double getUrlsPerTweet() { return urlsPerTweet; }

    public double getMediasPerTweet() { return mediasPerTweet; }

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

    public void setSymbolsPerTweet(double symbolsPerTweet) { this.symbolsPerTweet = symbolsPerTweet; }

    public void setUrlsPerTweet(double urlsPerTweet) { this.urlsPerTweet = urlsPerTweet; }

    public void setMediasPerTweet(double mediasPerTweet) { this.mediasPerTweet = mediasPerTweet; }

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
                            +this.symbolsPerTweet + TrendVector.VALUE_SEPARATOR
                            +this.urlsPerTweet + TrendVector.VALUE_SEPARATOR
                            +this.mediasPerTweet;

        return vectorCsv;
    }
}
