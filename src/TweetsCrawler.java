/**
 * Created by panossakkos on 2/12/14.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

public class TweetsCrawler extends Thread {

    private static final String TWEETS_FOLDER = "tweets";
    private static final String CONSUMER_KEY = "ERLRoYHJSuCuMn2iG8Z2w";
    private static String ACCESS_TOKEN = "40974174-Jy3bKCgVykJM6i0eN27f02CS0liqWkLTd8TdAsEaR";

    private Object isCrawlingLock = new Object();
    private boolean isCrawling = false;

    private Twitter twitter;

    public TweetsCrawler () throws Exception {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, Secrets.getConsumerSecret());
        AccessToken oathAccessToken = new AccessToken(ACCESS_TOKEN, Secrets.getAccessTokenSecret());
        twitter.setOAuthAccessToken(oathAccessToken);
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
    }

    @Override public void run () {
        synchronized (this.isCrawlingLock) {
            this.isCrawling = true;
        }

        while (true) {
            synchronized (this.isCrawlingLock) {
                if (this.isCrawling == false) {
                    // TODO clean up

                    return;
                }
            }

            /* Crawl */

            // TODO
        }
    }
}
