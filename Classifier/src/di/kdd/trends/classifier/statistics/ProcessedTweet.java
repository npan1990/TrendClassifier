package di.kdd.trends.classifier.statistics;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.text.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by panos on 2/25/14.
 */

public class ProcessedTweet {

    private String id;
    private String userName;
    private Date time;
    private Boolean isRwetweet;
    private Boolean fromSearch;
    private int retweetCount;
    private ArrayList<String> tokens = new ArrayList<String>();
    private ArrayList<String> hashTags = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> replies = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Date getTime() {
        return time;
    }

    public Boolean getIsRwetweet() {
        return isRwetweet;
    }

    public Boolean isFromSearch() { return fromSearch; }

    public int getRetweetCount() {
        return retweetCount;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getHashTags() {
        return hashTags;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public ArrayList<String> getReplies() {
        return replies;
    }

    public ProcessedTweet(String fromString) throws ParseException {
        String []split = fromString.split("\\|", -1);


        this.id = split[1];
        this.userName = split[2];
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.time = dateFormat.parse(split[3]);
        this.isRwetweet = Boolean.parseBoolean(split[4]);
        this.retweetCount = Integer.parseInt(split[5]);
        this.fromSearch = Integer.parseInt(split[0])==1;

        String []tokens = split[6].split(",", -1);

        for (String token : tokens) {
            this.getTokens().add(token);
        }

        String []hashTags = split[7].split(",", -1);

        for (String hashTag : hashTags) {
            hashTag = hashTag.replace("#", "");

            if (hashTag.length() > 0) {
                this.getHashTags().add(hashTag.replace("#", ""));
            }
        }

        String []urls = split[8].split(",", -1);

        for (String url : urls) {
            if (url.length() > 0) {
                this.getUrls().add(url);
            }
        }

        String []replies = split[9].split(",", -1);

        for (String reply : replies) {
            if (reply.length() > 0) {
                this.getReplies().add(reply);
            }
        }
    }
}
