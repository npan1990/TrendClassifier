package di.kdd.trends.classifier.crawler;

import di.kdd.trends.classifier.crawler.config.Location;
import di.kdd.trends.classifier.crawler.config.Token;
import di.kdd.trends.classifier.crawler.config.TokenLoader;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Map;

/**
 * Encapsulates <UberTwitter.HOW_MANY> Twitter objects in one Uber class.
 *
 * Created by panos on 3/6/14.
 */

public class UberTwitter {

    public enum What {Trends, Stream, Search};

    private static int HOW_MANY = 12;

    private static int TREND_TOKEN = 0;
    private static int STREAM_TOKEN = 1;

    ArrayList<Twitter> twitterz = new ArrayList<Twitter>();

    public UberTwitter(Location location) throws Exception {

        TokenLoader tokenLoader = new TokenLoader();

        for (int i = location.getId(); i < location.getId() + UberTwitter.HOW_MANY; i++) {
            Token token = tokenLoader.getToken(i);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
            AccessToken oathAccessToken = new AccessToken(token.getAccess(), token.getAccessSecret());
            twitter.setOAuthAccessToken(oathAccessToken);
            this.twitterz.add(twitter);
        }
    }

    public int getRemainingRateLimit (What what, String of) throws TwitterException {
        Map<String ,RateLimitStatus> rateLimitStatus;

        switch (what) {
            case Trends:
                rateLimitStatus = this.twitterz.get(UberTwitter.TREND_TOKEN).getRateLimitStatus();
                return rateLimitStatus.get(of).getRemaining();
            case Search:
                rateLimitStatus = this.twitterz.get(UberTwitter.STREAM_TOKEN).getRateLimitStatus();
                return rateLimitStatus.get(of).getRemaining();
            case Stream:
                return 0;
            default:
                return 0;
        }
    }


    public Trends getPlaceTrends(int woeid) throws TwitterException {
        return this.twitterz.get(UberTwitter.TREND_TOKEN).getPlaceTrends(woeid);
    }

    public QueryResult getStream(Query query) throws TwitterException {
        return this.twitterz.get(UberTwitter.STREAM_TOKEN).search(query);
    }

    public QueryResult search(Query queryTrend) throws TwitterException {
        return null;
    }

}
