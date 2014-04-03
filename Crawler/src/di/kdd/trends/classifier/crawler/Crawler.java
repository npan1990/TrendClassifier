package di.kdd.trends.classifier.crawler;

/**
 * Created by panossakkos on 2/12/14.
 */

import di.kdd.trends.classifier.crawler.config.Location;
import twitter4j.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import di.kdd.trends.classifier.crawler.UberTwitter.What;

public class Crawler extends Thread implements StatusListener{

    private static String TWEETS_FILE = "tweets";
    private static String TRENDS_FILE = "trends";

    private static int STREAM_TAG = 0;
    private static int SEARCH_TAG = 1;

    private static int RATE_LIMIT_FLOOR = 10;

    private static int TRENDS_CRAWL_INTERVAL = 5 * 60 * 1000; // 5 Minutes (in millis)
    private static int SEARCH_TREND_CRAWL_INTERVAL =  5 * 1000; // 5 Seconds (in millis)

    private boolean isCrawling = false;

    private UberTwitter twitter;
    private ArrayList<String> crawledTrends = new ArrayList<String>();
    private Location location;
    private PrintWriter tweetsWriter, trendsWriter;

    public Crawler(Location location) throws Exception {
        this.twitter = new UberTwitter(location);

        this.location = location;
    }

    private synchronized void startCrawling () {
        this.isCrawling = true;
    }

    public synchronized boolean isCrawling () {
        return this.isCrawling;
    }

    public synchronized void stopCrawling () {
        this.isCrawling = false;
        this.flushWriters();
        System.out.println(this.getLogTag() + "Crawler " + this.location.getName() + " stopped");
    }

    @Override public void run () {
        this.startCrawling();

        try {
            this.setupFileSystem();

            this.initializeWriters();
        }
        catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.err.println(this.getLogTag() + "Failed to start " + this.location.getName() + " crawler");
        }

        System.out.println(this.getLogTag() + "Crawler for " + this.location.getName() + " started");

        DateFormat dateFormat = new SimpleDateFormat("dd");
        String previousDay = dateFormat.format(new Date());

        this.setUpTwitterStreamCrawling();

        while (this.isCrawling()) {
            String currentDay = dateFormat.format(new Date());

            if (!currentDay.equals(previousDay)) {
                crawledTrends.clear();
                previousDay = currentDay;
            }

            this.crawlTrends();
            this.crawlTweetsWithTrends();
        }

        this.flushWriters();
        this.closeWriters();
    }

    private void setUpTwitterStreamCrawling() {

        FilterQuery filterQuery = new FilterQuery();
        double [][] location = new double[1][4];
        location[0][0] = this.location.getSwlong();
        location[0][1] = this.location.getSwlat();
        location[0][2] = this.location.getNelong();
        location[0][3] = this.location.getNelat();

        filterQuery.locations(location);

        try {
            this.twitter.startStream(this, filterQuery);
        }
        catch(Exception exception) {
            System.err.println("Failed to start crawling twitter stream");
        }
    }

    private long lastTrendCrawl;
    private long lastSearchCrawl;

    private void crawlTrends () {
        long now = System.currentTimeMillis();

        if (lastTrendCrawl != 0) {

            /* Check if TRENDS_CRAWL_INTERVAL elapsed since last trend crawling */
            
            if (now - this.lastTrendCrawl < Crawler.TRENDS_CRAWL_INTERVAL) {
                return;
            }
        }

        try {

            /* Check rate limit */

            if (this.twitter.getRemainingRateLimit(What.Trends) < Crawler.RATE_LIMIT_FLOOR) {
                System.out.println(this.getLogTag() + "Hit rate limit floor (" + Crawler.RATE_LIMIT_FLOOR + ") for trends crawling");
                return;
            }

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            Trend[] trends = twitter.getPlaceTrends(this.location.getWoeid()).getTrends();

            Date date = new Date();
            this.trendsWriter.println(dateFormat.format(date));

            for (Trend trend : trends) {
                this.trendsWriter.println(trend.getName());

                if (this.crawledTrends.contains(trend.getName()) == false) {
                    this.crawledTrends.add(trend.getName());
                }
            }

            this.trendsWriter.flush();
            this.lastTrendCrawl = now;

            System.out.println(this.getLogTag() + "Got trends. Left: " + this.twitter.getRemainingRateLimit(What.Trends));
        }
        catch (Exception exception) {
            System.err.println(this.getLogTag() + "Failed to crawl trends");
            System.err.println(exception.getMessage());
        }
    }


    private void crawlTweetsWithTrends() {
        long now = System.currentTimeMillis();

        if (lastSearchCrawl != 0) {

            /* Check if TWEETS_CRAWL_INTERVAL elapsed since last trend crawling */

            if (now - this.lastSearchCrawl < Crawler.SEARCH_TREND_CRAWL_INTERVAL * this.crawledTrends.size()) {
                return;
            }
        }

        for (String crawledTrend : this.crawledTrends) {
            Query queryTrend = new Query(crawledTrend);
            queryTrend.geoCode(new GeoLocation(this.location.getLatitude(), this.location.getLongitude()), this.location.getRadius(), Query.KILOMETERS.toString());
            queryTrend.lang("en");

            try {

                /* Check rate limit */

                if (this.twitter.getRemainingRateLimit(What.Search) < Crawler.RATE_LIMIT_FLOOR) {
                    System.out.println(this.getLogTag() + "Hit rate limit floor (" + Crawler.RATE_LIMIT_FLOOR + ") for trend search crawling");
                    return;
                }

                QueryResult queryResult = this.twitter.search(queryTrend);

                for (Status status : queryResult.getTweets()) {
                    printMetaInfo(status, Crawler.SEARCH_TAG);
                    printText(status);
                }

                this.tweetsWriter.flush();
                this.lastSearchCrawl = now;

                System.out.println(this.getLogTag() + "Got " + queryResult.getCount() + " tweets containing trend: " + crawledTrend + ". Left: " + this.twitter.getRemainingRateLimit(What.Search));
            }
            catch (TwitterException exception) {
                System.err.println(this.getLogTag() + "Failed to crawl tweets with trend " + crawledTrend);
                System.err.println(exception.getMessage());
            }
        }
    }

    private void initializeWriters() throws IOException {
        FileOutputStream fileStream = new FileOutputStream(tweetsFile(), true);
        OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");
        tweetsWriter = new PrintWriter(new BufferedWriter(writer));
        trendsWriter = new PrintWriter(new BufferedWriter(new FileWriter(trendsFile(), true)));
    }

    private void flushWriters () {
        this.tweetsWriter.flush();
        this.trendsWriter.flush();
    }

    private void closeWriters() {
        this.tweetsWriter.close();
        this.trendsWriter.close();
    }

    private String tweetsFile () {
        return Application.DATA_FOLDER + this.location.getName() + "/" + Crawler.TWEETS_FILE;
    }

    private String trendsFile () {
        return Application.DATA_FOLDER + this.location.getName() + "/" + Crawler.TRENDS_FILE;
    }

    private void setupFileSystem() throws Exception {
        File locationDir = new File(Application.DATA_FOLDER + this.location.getName());

        if (locationDir.exists() == false) {
            locationDir.mkdir();
            (new File(this.tweetsFile())).createNewFile();
            (new File(this.trendsFile())).createNewFile();
        }
    }

    private Date getDate() {
        return new Date(System.currentTimeMillis());
    }

    public Object getLogTag() {
        return "[" + location.getName() + " " + this.getDate() + "]: ";
    }

    protected void printMetaInfo(Status status, int tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag +
                "|" + status.getId() +
                "|" + status.getUser().getScreenName() +
                "|" + status.getCreatedAt().toString() +
                "|" + Boolean.toString(status.getInReplyToUserId() != -1) +
                "|" + status.isRetweet() +
                "|" + status.getRetweetCount() +
                "|" + status.getFavoriteCount()
        );

        // Lists of entities
        ArrayList<String> symbols = new ArrayList<String>();
        for (SymbolEntity se : status.getSymbolEntities()){
            symbols.add(se.getText().replace("|", "").replace(",", ""));
        }
        sb.append("|" + symbols + "|" + symbols.size());

        ArrayList<String> hashtags = new ArrayList<String>();
        for (HashtagEntity he : status.getHashtagEntities()){
            hashtags.add(he.getText().replace("|", "").replace(",", ""));
        }
        sb.append("|" + hashtags + "|" + hashtags.size());

        ArrayList<String> urls = new ArrayList<String>();
        for (URLEntity url : status.getURLEntities()){
            urls.add(url.getExpandedURL().replace("|", "").replace(",", ""));

        }
        sb.append("|" + urls + "|" + urls.size());

        ArrayList<String> ums = new ArrayList<String>();
        for (UserMentionEntity um : status.getUserMentionEntities()){
            ums.add(um.getText().replace("|", "").replace(",", ""));
        }
        sb.append("|" + ums + "|" + ums.size());

        ArrayList<String> media = new ArrayList<String>();
        for (MediaEntity medium : status.getMediaEntities()){
            media.add(medium.getType().replace("|", "").replace(",", ""));
        }
        sb.append("|" + media + "|" + media.size());

        tweetsWriter.println(sb.toString().replace("[", "").replace("]", ""));
    }

    protected void printText(Status status) {
        String text = status.getText().replace("\n", " ").replace("\r", " ").replace("\r\n", " ");
        tweetsWriter.println(text);
    }

    /* StatusListener interface */

    public void onStatus(Status status) {
        this.printMetaInfo(status, Crawler.STREAM_TAG);
        this.printText(status);
        this.tweetsWriter.flush();
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
    public void onScrubGeo(long l, long l2) {}
    public void onStallWarning(StallWarning stallWarning) {}

    public void onException(Exception ex) {
        System.err.println("Error while crawling stream: " + ex.getMessage());
        ex.printStackTrace();
    }

}
