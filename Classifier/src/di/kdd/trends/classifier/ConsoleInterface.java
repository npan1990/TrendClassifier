package di.kdd.trends.classifier;

import di.kdd.trends.classifier.statistics.Application;
import di.kdd.trends.classifier.statistics.Statistics;

import java.util.Scanner;

/**
 * Created by panos on 2/28/14.
 */

public class ConsoleInterface {

    public static void main (String []args) throws Exception {
        if (args.length < 2) {
            System.err.println("Processed tweets file and trends file are needed as arguments");
            return;
        }

        String location = args[0];
        String date = args[1];

        System.out.println("Processing data for " + args[0] + " " + args[1]);
        Statistics.load(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE,
                Application.DATA_FOLDER + location + "/" + date + Application.TRENDS_FILE);
        System.out.println("Finished processing");

        String command;
        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");
        while ((command = scanner.nextLine()) != null) {
            String tokens[] = command.split(" ");

            if (tokens.length == 1 && tokens[0].compareTo("all") == 0) {
                Statistics.sense();
            }
            else if (tokens.length == 1 && tokens[0].compareTo("ls") == 0) {
                Statistics.list();
            }
            else if (tokens.length == 1 && tokens[0].compareTo("q") == 0) {
                return;
            }
            else if (tokens.length > 0) {
                for (int i = 0; i < tokens.length; i++) {
                    Statistics.senseTrend(tokens[i]);
                }
            }

            System.out.print("> ");
        }
    }

}
