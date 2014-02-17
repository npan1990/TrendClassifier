/**
 * Created by panossakkos on 2/12/14.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.util.Map;

public class Crawler extends Thread {

    private static String TWEETS_FILE = "tweets";
    private static String TRENDS_FILE = "trends";

    private Object isCrawlingLock = new Object();
    private boolean isCrawling = false;

    private Twitter twitter;
    private Location location;
    private PrintWriter tweetsWriter, trendsWriter;

    public Crawler(Location location, Token token) throws Exception {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
        AccessToken oathAccessToken = new AccessToken(token.getAccess(), token.getAccessSecret());
        twitter.setOAuthAccessToken(oathAccessToken);

        this.location = location;
    }

    public String getCrawlerName () {
        return this.location.getName();
    }

    public boolean isCrawling () {
        boolean crawling;

        synchronized (this.isCrawlingLock) {
            crawling = this.isCrawling;
        }

        return crawling;
    }

    public void stopCrawling () {
        synchronized (this.isCrawlingLock) {
            this.isCrawling = false;
        }

        this.flushWriters();

        System.out.println("Crawler " + this.location.getName() + " stopped");
    }

    @Override public void run () {
        synchronized (this.isCrawlingLock) {
            this.isCrawling = true;
        }

        try {
            this.setupFileSystem();

            this.initializeWriters();
        }
        catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.err.println("Failed to start " + this.location.getName() + " crawler");
        }

        System.out.println("Crawler for " + this.location.getName() + " started");

        while (true) {
            synchronized (this.isCrawlingLock) {
                if (this.isCrawling == false) {
                    tweetsWriter.close();
                    return;
                }
            }

            /* Crawl */

            this.crawlTrends();
            this.crawlTweets();
        }
    }

    private void crawlTweets() {
        Query query = new Query("");
        query.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);

        try {

            this.rateLimitWatchDog("/search/tweets");

            QueryResult queryResult = this.twitter.search(query);

            for (Status status : queryResult.getTweets()) {
                tweetsWriter.println(
                        status.getId() +
                        " " + status.getUser().getScreenName() +
                        " " + status.getCreatedAt().toString() +
                        " " + status.isRetweet() +
                        " " + status.getRetweetCount());

                tweetsWriter.println(status.getText());
            }
        }
        catch (TwitterException exception) {
            System.err.println(exception.getMessage());
        }

    }

    private void crawlTrends () {
        // TODO
    }

    private void initializeWriters() throws IOException {
        tweetsWriter = new PrintWriter(new BufferedWriter(new FileWriter(tweetsFile(), true)));
        trendsWriter = new PrintWriter(new BufferedWriter(new FileWriter(trendsFile(), true)));
    }

    private void flushWriters () {
        this.tweetsWriter.flush();
        this.trendsWriter.flush();
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
            if (this.getRemainingRateLimit(what) < 100) {

                int sleepSeconds = this.getSecondsUntilReset(what);

                System.out.println(this.location.getName() + " close to rate limit, going to sleep for " + sleepSeconds);

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
