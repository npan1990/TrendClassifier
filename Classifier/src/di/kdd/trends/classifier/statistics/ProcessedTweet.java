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

    private User user;



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

    public User getUser() { return user; }

//    public ArrayList<String> getUrls() {
//        return urls;
//    }

    public ArrayList<String> getMentions() {
        return mentions;
    }


    //Tokenizer output
    //(0)fromSearch, (1)tweetId, (2)userName, (3)userId, (4)userVerified, (5)userFollowersCount, (6)userFriendsCount,
    //(7)userListedCount, (8)userStatusesCount, (9)dateStr, (10)inReplyToUserId, (11)isRetweet, (12)retweetCount,
    //(13)favoriteCount, (14)hashtags, (15)hashtagsCount, (16)urls, (17)urlsCount, (18)user_mentions, (19)user_mentionsCount
    //(20)media, (21)mediaCount, (22)tokens, (23)raw_tweet



    public ProcessedTweet(String fromString) throws ParseException {
        String []split = fromString.split("\\|", -1);

        this.fromSearch = Integer.parseInt(split[0]) == 1;
        this.id = split[1];
        this.userName = split[2];
        //Tue Apr 01 10:34:06 +0000 2014
        this.time = (new SimpleDateFormat("HH:mm:ss")).parse(split[9].split(" ")[3]);

        if ( split[10].equals("None")) {
            this.isReply = false;
        }
        else {
            this.isReply = true;
        }

        this.isRetweet = Boolean.parseBoolean(split[11]);
        this.retweetCount = Integer.parseInt(split[12]);
        this.favoriteCount = Integer.parseInt(split[13]);
        this.urlsCount = Integer.parseInt(split[17]);
        this.mediaCount = Integer.parseInt(split[21]);

        String []tokens = split[22].split(",", -1);
        for (String token : tokens) {
            if (token.length() > 0) {
                this.getTokens().add(token);
            }
        }

        String []hashTags = split[14].split(",", -1);
        for (String hashTag : hashTags) {
            if (hashTag.length() > 0) {
                this.getHashTags().add(hashTag.trim().toLowerCase());
            }
        }

        String []mentions = split[18].split(",", -1);
        for (String mention : mentions) {
            if (mention.length() > 0) {
                this.getMentions().add(mention);
            }
        }

        this.rawTweet = split[23];

        //(3)userId, (4)userVerified, (5)userFollowersCount, (6)userFriendsCount,
        //(7)userListedCount, (8)userStatusesCount,

        //User information
        Boolean userVerified;

        if ("True".equals(split[4])) { userVerified = true; }
        else { userVerified = false; }

        long userId = Long.parseLong(split[3]);
        long userFollowersCount = Long.parseLong(split[5]);
        long userFriendsCount = Long.parseLong(split[6]);
        long userListedCount = Long.parseLong(split[7]);
        long userStatusesCount = Long.parseLong(split[8]);

        this.user = new User(userId, userFollowersCount, userFriendsCount, userVerified, userStatusesCount, userListedCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessedTweet that = (ProcessedTweet) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
