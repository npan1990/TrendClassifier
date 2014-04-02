package di.kdd.trends.classifier.crawler;

import di.kdd.trends.classifier.crawler.config.Location;
import di.kdd.trends.classifier.crawler.config.Token;
import di.kdd.trends.classifier.crawler.config.TokenLoader;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Map;

/**
 * Encapsulates {@valueUberTwitter.SEARCH_TOKENS} Twitter objects in one Uber class.
 *
 * Created by panos on 3/6/14.
 */

public class UberTwitter {

    public enum What {Trends, Search};

    private static int SEARCH_TOKENS = 10;
    private static int NO_TOKENS = 1 + 1 + SEARCH_TOKENS;

    private static int STREAM_TOKEN = 0;
    private static int TREND_TOKEN = STREAM_TOKEN + 1;
    private static int FIRST_ACTIVE = TREND_TOKEN + 1;
    private static int LAST_ACTIVE = NO_TOKENS - 1;

    private TwitterStream twitterStream = null;
    private ArrayList<Twitter> twitterz = new ArrayList<Twitter>();

    private int activeToken = FIRST_ACTIVE;

    public UberTwitter(Location location) throws Exception {

        TokenLoader tokenLoader = new TokenLoader();

        for (int i = location.getId() * UberTwitter.NO_TOKENS; i < (location.getId() * UberTwitter.NO_TOKENS) + UberTwitter.NO_TOKENS; i++) {

            if (this.twitterStream == null) {

                /* Set up authentication token for stream */

                Token token = tokenLoader.getToken(i);
                this.twitterStream = TwitterStreamFactory.getSingleton();
                this.twitterStream.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
                AccessToken oathAccessToken = new AccessToken(token.getAccess(), token.getAccessSecret());
                this.twitterStream.setOAuthAccessToken(oathAccessToken);

                continue;
            }

            Token token = tokenLoader.getToken(i);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
            AccessToken oathAccessToken = new AccessToken(token.getAccess(), token.getAccessSecret());
            twitter.setOAuthAccessToken(oathAccessToken);
            this.twitterz.add(twitter);
        }
    }

    private int nextToken () {
        if (this.activeToken == UberTwitter.LAST_ACTIVE - 1) {
            return UberTwitter.FIRST_ACTIVE;
        }
        else {
            return this.activeToken + 1;
        }
    }

    private int previousToken () {
        if (this.activeToken == UberTwitter.FIRST_ACTIVE) {
            return UberTwitter.LAST_ACTIVE - 1;
        }
        else {
            return this.activeToken - 1;
        }
    }

    public int getRemainingRateLimit (What what) throws TwitterException {
        Map<String, RateLimitStatus> rateLimitStatus;

        switch (what) {
            case Trends:
                rateLimitStatus = this.twitterz.get(UberTwitter.TREND_TOKEN).getRateLimitStatus();
                return rateLimitStatus.get("/trends/place").getRemaining();
            case Search:
                rateLimitStatus = this.twitterz.get(previousToken()).getRateLimitStatus();
                return rateLimitStatus.get("/search/tweets").getRemaining();
            default:
                return 0;
        }
    }

    public Trends getPlaceTrends(int woeid) throws TwitterException {
        return this.twitterz.get(UberTwitter.TREND_TOKEN).getPlaceTrends(woeid);
    }

    public void startStream(StatusListener statusListener) throws TwitterException {
        this.twitterStream.addListener(statusListener);
        this.twitterStream.sample();
    }

    public QueryResult search(Query queryTrend) throws TwitterException {
        QueryResult queryResult =  this.twitterz.get(this.activeToken).search(queryTrend);

        this.activeToken = this.nextToken();

        return queryResult;
    }

}
