/**
 * Created by panossakkos on 2/12/14.
 */

import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;

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

        System.out.println("Crawler for " + this.location.getName() + " created");
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

        Query query = new Query("");
        query.geoCode(new GeoLocation(this.location.getLongitude(), this.location.getLatitude()), this.location.getRadius(), Query.KILOMETERS);

        PrintWriter out = null;

        while (true) {
            synchronized (this.isCrawlingLock) {
                if (this.isCrawling == false) {
                    out.close();
                    return;
                }
            }

            /* Crawl */


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


            try {
                QueryResult queryResult = this.twitter.search(query);

                for (Status status : queryResult.getTweets()) {
                    out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                }
            }
            catch (TwitterException exception) {
                System.err.println(exception.getMessage());
            }
        }
    }
}
