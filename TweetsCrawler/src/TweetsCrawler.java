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
    private PrintWriter tweetsWriter;

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

        this.tweetsWriter.flush();

        System.out.println("Crawler " + this.location.getName() + " stopped");
    }

    @Override public void run () {
        synchronized (this.isCrawlingLock) {
            this.isCrawling = true;
        }

        File tweetsFile = new File(Application.TWEETS_FOLDER + this.location.getName());

        try {
            if (tweetsFile.exists() == false) {
                tweetsFile.createNewFile();
            }

            tweetsWriter = new PrintWriter(new BufferedWriter(new FileWriter(tweetsFile, true)), true);
        }
        catch (IOException exception) {
            System.err.println("Failed to create location file");
            return;
        }

        System.out.println("Crawler for " + this.location.getName() + " started");

        Query query = new Query("");
        query.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);

        while (true) {
            synchronized (this.isCrawlingLock) {
                if (this.isCrawling == false) {
                    tweetsWriter.close();
                    return;
                }
            }

            /* Crawl */

            try {

                this.rateLimitWatchDog();

                QueryResult queryResult = this.twitter.search(query);

                for (Status status : queryResult.getTweets()) {
                    tweetsWriter.println("@" + status.getUser().getScreenName() + " " + status.getCreatedAt().toString());
                    tweetsWriter.println(status.getText());
                }
            }
            catch (TwitterException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }

    private void rateLimitWatchDog () {
        try {
            if (this.getRemainingRateLimit("/search/tweets") < 10) {

                int sleepSeconds = this.getSecondsUntilReset("/search/tweets");

                System.out.println(this.location.getName() + " close to rate limit, going to sleep for " + sleepSeconds);

                tweetsWriter.flush();
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
