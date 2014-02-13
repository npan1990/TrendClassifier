/**
 * Created by panossakkos on 2/13/14.
 */

import java.io.*;

public class Secrets {

    private static final String SECRETS_FOLDER = "Secrets/";
    private static final String CONSUMER = "CONSUMER";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static String getConsumerSecret () throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Secrets.SECRETS_FOLDER + Secrets.CONSUMER));
        StringBuilder sb = new StringBuilder();
        String consumer = br.readLine();
        br.close();

        return consumer;
    }

    public static String getAccessTokenSecret () throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(Secrets.SECRETS_FOLDER + Secrets.ACCESS_TOKEN));
        StringBuilder sb = new StringBuilder();
        String accessToken = br.readLine();


        return accessToken;
    }

}
