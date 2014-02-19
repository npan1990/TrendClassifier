package di.kdd.trends.classifier.crawler;

/**
 * Created by panossakkos on 2/12/14.
 */

import di.kdd.trends.classifier.crawler.config.Location;
import di.kdd.trends.classifier.crawler.config.Token;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Crawler extends Thread {

    private static String TWEETS_FILE = "tweets";
    private static String TRENDS_FILE = "trends";

    private static int TRENDS_CRAWL_INTERVAL = 5 * 60 * 1000; // 5 Minutes (in millis)
    private static int TWEETS_CRAWL_INTERVAL =  10 * 1000; // 10 Seconds (in millis)
    private boolean isCrawling = false;

    private Twitter twitter;
    private Location location;
    private PrintWriter tweetsWriter, trendsWriter;

    private String LOGTAG;
    public Crawler(Location location, Token token) {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
        AccessToken oathAccessToken = new AccessToken(token.getAccess(), token.getAccessSecret());
        twitter.setOAuthAccessToken(oathAccessToken);

        this.location = location;
        LOGTAG = "[" + location.getName() + "]:";
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
            this.crawlTweets();
        }

        this.flushWriters();
        this.closeWriters();
    }

    private long lastTrendCrawl;
    private long lastTweetCrawl;

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
            }

            this.trendsWriter.flush();
            this.lastTrendCrawl = now;
            System.out.println(LOGTAG + "Got trends. Left: " + getRemainingRateLimit("/trends/place"));
        }
        catch (Exception exception) {
            System.err.println(LOGTAG + "Failed to crawl trends");
            System.err.println(exception.getMessage());
        }
    }

    private void crawlTweets() {

        long now = System.currentTimeMillis();

        if (lastTweetCrawl != 0) {

            /* Check if TWEETS_CRAWL_INTERVAL elapsed since last trend crawling */
            if (now - this.lastTweetCrawl < Crawler.TWEETS_CRAWL_INTERVAL) {
                return;
            }
        }

        Query query = new Query("");
        query.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);
        query.lang("en");
        try {

            QueryResult queryResult = this.twitter.search(query);

            for (Status status : queryResult.getTweets()) {
                tweetsWriter.println(
                        status.getId() +
                        " " + status.getUser().getScreenName() +
                        " " + status.getCreatedAt().toString() +
                        " " + status.isRetweet() +
                        " " + status.getRetweetCount());

                String text = status.getText().replace("\n", " ").replace("\r", " ").replace("\r\n", " ");
                tweetsWriter.println(text);
            }
            this.tweetsWriter.flush();
            this.lastTweetCrawl = now;
            System.out.println(LOGTAG + "Got tweets. Left: " + getRemainingRateLimit("/search/tweets"));
        }
        catch (TwitterException exception) {
            System.err.println(LOGTAG + "Failed to crawl tweets");
            System.err.println(exception.getMessage());
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

    private void rateLimitWatchDog (String what) {
        try {
            if (this.getRemainingRateLimit(what) < 10) { //TODO change the guard number

                int sleepSeconds = this.getSecondsUntilReset(what);

                System.out.println(this.location.getName() + " close to rate limit, going to sleep for " + sleepSeconds + " seconds");

                this.flushWriters();
                Thread.sleep(sleepSeconds * 1000);
            }
        }
        catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    private int getRemainingRateLimit (String of) throws TwitterException {
        Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
        return rateLimitStatus.get(of).getRemaining();
    }

    private int getSecondsUntilReset (String of) throws TwitterException {
        Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
        return rateLimitStatus.get(of).getSecondsUntilReset();
    }
}
