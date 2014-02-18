package di.kdd.trends.classifier.crawler.config; /**
 * Created by panossakkos on 2/13/14.
 */

import di.kdd.trends.classifier.crawler.Application;

import java.io.*;
import java.util.ArrayList;

public class TokenLoader {

    private static final String CONSUMER_FILE = "CONSUMER";
    private static final String ACCESS_TOKEN_FILE = "ACCESS_TOKEN";

    private int tokenIndex = 0;
    private ArrayList<Token> tokens = new ArrayList<Token>();

    public TokenLoader () throws Exception {
        BufferedReader consumerReader = new BufferedReader(new FileReader(Application.PUBLIC_TOKENS_FOLDER + TokenLoader.CONSUMER_FILE));
        BufferedReader consumerSecretReader = new BufferedReader(new FileReader(Application.SECRET_TOKENS_FOLDER + TokenLoader.CONSUMER_FILE));
        BufferedReader accessReader = new BufferedReader(new FileReader(Application.PUBLIC_TOKENS_FOLDER + TokenLoader.ACCESS_TOKEN_FILE));
        BufferedReader accessSecretReader = new BufferedReader(new FileReader(Application.SECRET_TOKENS_FOLDER + TokenLoader.ACCESS_TOKEN_FILE));

        String consumerLine;

        while ((consumerLine = consumerReader.readLine()) != null) {
            String consumerSecretLine = consumerSecretReader.readLine();
            String accessLine = accessReader.readLine();
            String accessSecretLine = accessSecretReader.readLine();

            this.tokens.add(new Token(consumerLine, consumerSecretLine, accessLine, accessSecretLine));
        }

    }

    public Token getToken () {

        if (this.tokenIndex == this.tokens.size()) {
            return null;
        }

        return this.tokens.get(this.tokenIndex++);
    }
}
