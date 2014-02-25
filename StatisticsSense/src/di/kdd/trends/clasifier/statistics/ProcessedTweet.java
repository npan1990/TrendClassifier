package di.kdd.trends.clasifier.statistics;

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
    private int retweetCount;
    private ArrayList<String> tokens = new ArrayList<String>();
    private ArrayList<String> hashTags = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> replies = new ArrayList<String>();

    public ProcessedTweet(String fromString) throws ParseException {
        String []split = fromString.split(";");

        for (String lol : split) {
            System.out.println(lol);
        }

        this.id = split[0];
        this.userName = split[1];
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.time = dateFormat.parse(split[2]);
        this.isRwetweet = Boolean.parseBoolean(split[3]);
        this.retweetCount = Integer.parseInt(split[4]);

        String []tokens = split[5].split(",");

        for (String token : tokens) {
            this.tokens.add(token);
        }

        String []hashTags = split[6].split(",");

        for (String hashTag : hashTags) {
            this.hashTags.add(hashTag.replace("#", ""));
        }

        String []urls = split[7].split(",");

        for (String url : urls) {
            this.urls.add(url);
        }

        String []replies = split[8].split(",");

        for (String reply : replies) {
            this.replies.add(reply);
        }
    }
}
