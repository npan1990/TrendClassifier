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
    private int favoriteCount;
    private int urlsCount;
    private int mediaCount;
    private String rawTweet;

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

    public Boolean isReply() { return isReply; }

    public Boolean isRetweet() { return isRetweet; }

    public Boolean isFromSearch() { return fromSearch; }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return retweetCount;
    }

    public int getUrlsCount() { return urlsCount; }

    public int getMediaCount() { return mediaCount; }

    public String getRawTweet() { return rawTweet; }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getHashTags() {
        return hashTags;
    }

//    public ArrayList<String> getUrls() {
//        return urls;
//    }

    public ArrayList<String> getMentions() {
        return mentions;
    }


    //Tokenizer output
    //(0)fromSearch, (1)tweetId, (2)userName, (3)time, (4)isReply, (5)isRetweet, (6)retweetCount,
    //(7)favoriteCount, (8)symbolCount, (9)urlsCount, (10)mediaCount, (11)tokenList, (12)hashtagList,
    //(13)mentionList, (14)raw_tweet

    public ProcessedTweet(String fromString) throws ParseException {
        String []split = fromString.split("\\|", -1);

        this.fromSearch = Integer.parseInt(split[0]) == 1;
        this.id = split[1];
        this.userName = split[2];
        this.time = (new SimpleDateFormat("HH:mm:ss")).parse(split[3]);
        this.isReply = Boolean.parseBoolean(split[4]);
        this.isRetweet = Boolean.parseBoolean(split[5]);
        this.retweetCount = Integer.parseInt(split[6]);
        this.favoriteCount = Integer.parseInt(split[7]);
        this.urlsCount = Integer.parseInt(split[9]);
        this.mediaCount = Integer.parseInt(split[10]);

        String []tokens = split[11].split(",", -1);
        for (String token : tokens) {
            if (token.length() > 0) {
                this.getTokens().add(token);
            }
        }

        String []hashTags = split[12].split(",", -1);
        for (String hashTag : hashTags) {

            String rawhashTag = hashTag.toLowerCase().trim();

            if (rawhashTag.length() > 0 && !this.getHashTags().contains(rawhashTag)) {
                this.getHashTags().add(rawhashTag);
            }
        }

        String []mentions = split[13].split(",", -1);
        for (String mention : mentions) {
            if (mention.length() > 0) {
                this.getMentions().add(mention);
            }
        }

        this.rawTweet = split[14];
    }
}
