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

public class Crawler extends Thread {

    private static String TWEETS_FILE = "tweets";
    private static String TRENDS_FILE = "trends";

    private static int STREAM_TAG = 0;
    private static int SEARCH_TAG = 1;

    private static int TRENDS_CRAWL_INTERVAL = 5 * 60 * 1000; // 5 Minutes (in millis)
    private static int TWEETS_CRAWL_INTERVAL =  10 * 1000; // 10 Seconds (in millis)
    private boolean isCrawling = false;

    private UberTwitter twitter;
    private ArrayList<String> crawledTrends = new ArrayList<String>();
    private Location location;
    private PrintWriter tweetsWriter, trendsWriter;

    private String LOGTAG;

    public Crawler(Location location) throws Exception {
        this.twitter = new UberTwitter(location);

        this.location = location;
        LOGTAG = "[" + location.getName() + " " + this.getDate() + "]: ";
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
        System.out.println(LOGTAG + "Crawler " + this.location.getName() + " stopped");
    }

    @Override public void run () {
        this.startCrawling();

        try {
            this.setupFileSystem();

            this.initializeWriters();
        }
        catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.err.println(LOGTAG + "Failed to start " + this.location.getName() + " crawler");
        }

        System.out.println(LOGTAG + "Crawler for " + this.location.getName() + " started");

        while (this.isCrawling()) {
            this.crawlTrends();
            this.crawlStream();
            this.crawlTweetsWithTrends();
        }

        this.flushWriters();
        this.closeWriters();
    }

    private long lastTrendCrawl;
    private long lastStreamCrawl;
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

            System.out.println(LOGTAG + "Got trends. Left: " + this.twitter.getRemainingRateLimit(What.Trends, "/trends/place"));
        }
        catch (Exception exception) {
            System.err.println(LOGTAG + "Failed to crawl trends");
            System.err.println(exception.getMessage());
        }
    }

    private void crawlStream () {

        long now = System.currentTimeMillis();

        if (lastStreamCrawl != 0) {

            /* Check if TWEETS_CRAWL_INTERVAL elapsed since last trend crawling */

            if (now - this.lastStreamCrawl < Crawler.TWEETS_CRAWL_INTERVAL) {
                return;
            }
        }

        Query emptyQuery = new Query("");
        emptyQuery.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);
        emptyQuery.lang("en");

        try {

            QueryResult queryResult = this.twitter.getStream(emptyQuery);

            for (Status status : queryResult.getTweets()) {
                tweetsWriter.println(
                                Crawler.STREAM_TAG +
                                " " + status.getId() +
                                " " + status.getUser().getScreenName() +
                                " " + status.getCreatedAt().toString() +
                                " " + status.isRetweet() +
                                " " + status.getRetweetCount()
                );

                String text = status.getText().replace("\n", " ").replace("\r", " ").replace("\r\n", " ");
                tweetsWriter.println(text);
            }

            this.tweetsWriter.flush();
            this.lastStreamCrawl = now;

            System.out.println(LOGTAG + "Got stream. Left: " + this.twitter.getRemainingRateLimit(What.Stream, "/search/tweets"));
        }
        catch (TwitterException exception) {
            System.err.println(LOGTAG + "Failed to crawl tweets");
            System.err.println(exception.getMessage());
        }
    }

    private void crawlTweetsWithTrends() {
        long now = System.currentTimeMillis();

        if (lastStreamCrawl != 0) {

            /* Check if TWEETS_CRAWL_INTERVAL elapsed since last trend crawling */

            if (now - this.lastSearchCrawl < Crawler.TWEETS_CRAWL_INTERVAL * this.crawledTrends.size()) {
                return;
            }
        }

        for (String crawledTrend : this.crawledTrends) {
            Query queryTrend = new Query(crawledTrend);
            queryTrend.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);
            queryTrend.lang("en");

            try {

                QueryResult queryResult = this.twitter.search(queryTrend);

                for (Status status : queryResult.getTweets()) {
                    tweetsWriter.println(
                                    Crawler.SEARCH_TAG +
                                    " " + status.getId() +
                                    " " + status.getUser().getScreenName() +
                                    " " + status.getCreatedAt().toString() +
                                    " " + status.isRetweet() +
                                    " " + status.getRetweetCount()
                    );

                    String text = status.getText().replace("\n", " ").replace("\r", " ").replace("\r\n", " ");
                    tweetsWriter.println(text);
                }

                this.tweetsWriter.flush();
                this.lastSearchCrawl = now;

                System.out.println(LOGTAG + "Got tweets containing trend: " + crawledTrend + ". Left: " + this.twitter.getRemainingRateLimit(What.Search, "/search/tweets"));
            }
            catch (TwitterException exception) {
                System.err.println(LOGTAG + "Failed to crawl tweets with trend " + crawledTrend);
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
        return new Date();
    }
}
