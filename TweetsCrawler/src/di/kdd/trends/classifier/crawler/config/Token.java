package di.kdd.trends.classifier.crawler.config;

/**
 * Created by panossakkos on 2/17/14.
 */
public class Token {

    private String consumer, consumerSecret;
    private String access, accessSecret;

    public Token (String consumer, String consumerSecret, String access, String accessSecret) {
        this.consumer = consumer;
        this.consumerSecret = consumerSecret;
        this.access = access;
        this.accessSecret = accessSecret;
    }

    public String getConsumer () {
        return this.consumer;
    }

    public String getConsumerSecret () {
        return this.consumerSecret;
    }

    public String getAccess () {
        return this.access;
    }

    public String getAccessSecret () {
        return this.accessSecret;
    }
}
