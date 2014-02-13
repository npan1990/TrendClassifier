/**
 * Created by panossakkos on 2/13/14.
 */

import java.io.*;

public class Secrets {

    private static final String CONSUMER = "CONSUMER";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static String getConsumerSecret () throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Application.SECRETS_FOLDER + Secrets.CONSUMER));
        String consumer = br.readLine();
        br.close();

        return consumer;
    }

    public static String getAccessTokenSecret () throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Application.SECRETS_FOLDER + Secrets.ACCESS_TOKEN));
        String accessToken = br.readLine();
        br.close();

        return accessToken;
    }

}
