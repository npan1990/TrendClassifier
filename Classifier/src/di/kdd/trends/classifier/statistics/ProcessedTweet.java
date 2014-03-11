package di.kdd.trends.classifier.statistics;

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
    private Boolean isReply;
    private Boolean isRetweet;
    private Boolean fromSearch;
    private int retweetCount;
    private ArrayList<String> tokens = new ArrayList<String>();
    private ArrayList<String> hashTags = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> mentions = new ArrayList<String>();

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Date getTime() {
        return time;
    }

    public Boolean getIsReply() { return isReply; }

    public Boolean getIsRetweet() {
        return isRetweet;
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

    public ArrayList<String> getMentions() {
        return mentions;
    }

    public ProcessedTweet(String fromString) throws ParseException {
        String []split = fromString.split("\\|", -1);


        this.fromSearch = Integer.parseInt(split[0]) == 1;
        this.id = split[1];
        this.userName = split[2];
        this.time = (new SimpleDateFormat("HH:mm:ss")).parse(split[3]);
        this.isReply = Boolean.parseBoolean(split[4]);
        this.isRetweet = Boolean.parseBoolean(split[5]);
        this.retweetCount = Integer.parseInt(split[6]);

        String []tokens = split[7].split(",", -1);

        for (String token : tokens) {
            this.getTokens().add(token);
        }

        String []hashTags = split[8].split(",", -1);

        for (String hashTag : hashTags) {
            hashTag = hashTag.replace("#", "");

            if (hashTag.length() > 0) {
                this.getHashTags().add(hashTag.replace("#", ""));
            }
        }

        String []urls = split[9].split(",", -1);

        for (String url : urls) {
            if (url.length() > 0) {
                this.getUrls().add(url);
            }
        }

        String []replies = split[10].split(",", -1);

        for (String reply : replies) {
            if (reply.length() > 0) {
                this.getMentions().add(reply);
            }
        }
    }
}
