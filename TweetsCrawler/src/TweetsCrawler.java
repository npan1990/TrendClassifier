/**
 * Created by panossakkos on 2/12/14.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.util.Map;

public class TweetsCrawler extends Thread {

    private static final String CONSUMER_KEY = "ERLRoYHJSuCuMn2iG8Z2w";
    private static String ACCESS_TOKEN = "40974174-Jy3bKCgVykJM6i0eN27f02CS0liqWkLTd8TdAsEaR";

    private Object isCrawlingLock = new Object();
    private boolean isCrawling = false;

    private Twitter twitter;
    private Location location;

    public TweetsCrawler (Location location) throws Exception {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, Secrets.getConsumerSecret());
        AccessToken oathAccessToken = new AccessToken(ACCESS_TOKEN, Secrets.getAccessTokenSecret());
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

        System.out.println("Crawler " + this.location.getName() + " stopped");
    }

    @Override public void run () {
        synchronized (this.isCrawlingLock) {
            this.isCrawling = true;
        }

        PrintWriter out = null;
        File tweetsFile = new File(Application.TWEETS_FOLDER + this.location.getName());

        if (tweetsFile.exists() == false) {
            try {
                tweetsFile.createNewFile();
                out = new PrintWriter(new BufferedWriter(new FileWriter(tweetsFile, true)));
            }
            catch (IOException exception) {
                System.err.println("Failed to create location file");
                return;
            }
        }

        System.out.println("Crawler for " + this.location.getName() + " started");

        Query query = new Query("");
        query.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);

        while (true) {
            synchronized (this.isCrawlingLock) {
                if (this.isCrawling == false) {
                    out.close();
                    return;
                }
            }

            /* Crawl */

            try {

                this.rateLimitWatchDog();

                QueryResult queryResult = this.twitter.search(query);

                for (Status status : queryResult.getTweets()) {
                    out.println("@" + status.getUser().getScreenName() + " " + status.getCreatedAt().toString());
                    out.println(status.getText());

                    out.close();
                    return;
                }
            }
            catch (TwitterException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }

    private void rateLimitWatchDog () {
        try {
            if (this.getRemainingRateLimit("/search/tweets") < 100) {

                System.out.println(this.location.getName() + " close to rate limit, going to sleep");
                Thread.sleep(this.getResetTime("/search/tweets") * 1000);
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

    private int getResetTime(String of) throws TwitterException {
        Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
        return rateLimitStatus.get(of).getResetTimeInSeconds();
    }
}
