package di.kdd.trends.classifier;

import di.kdd.trends.classifier.statistics.Application;
import di.kdd.trends.classifier.statistics.Statistics;

import java.util.Scanner;

/**
 * Created by panos on 2/28/14.
 */

public class ConsoleInterface {


    public static void main (String []args) throws Exception {
        String date = null;
        String location = null;
//        if (args.length < 2) {
//            System.err.println("Processed tweets file and trends file are needed as arguments");
//            return;
//        }
//
//        String location = args[0];
//        String date = args[1];
//
//        System.out.println("Processing data for " + args[0] + " " + args[1]);
//        Statistics.load(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE,
//                Application.DATA_FOLDER + location + "/" + date + Application.TRENDS_FILE);
//        System.out.println("Finished processing");

        String command;
        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");
        while ((command = scanner.nextLine()) != null) {
            try {
                String tokens[] = command.split(",");

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
                    String[] split = tokens[0].split(" ");

                    // Set date
                    if (split[0].compareTo("d") == 0) {
                        date = split[1];
                        System.out.println("Date: " + date);
                        if (location == null) {
                            System.out.println("Location not set yet!");
                        }
                        else {
                            System.out.println("Location: " + location);
                            System.out.println("Processing data...");
                            Statistics.load(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE,
                                    Application.DATA_FOLDER + location + "/" + date + Application.TRENDS_FILE);
                            System.out.println("Finished processing!");
                        }
                    }

                    // Set location
                    else if (split[0].compareTo("l") == 0) {
                        location = split[1];
                        System.out.println("Location: " + location);
                        if (date == null) {
                            System.out.println("Date not set yet!");
                        }
                        else {
                            System.out.println("Date: " + date);
                            System.out.println("Processing data...");
                            Statistics.load(Application.DATA_FOLDER + location + "/" + date + Application.TWEETS_FILE,
                                    Application.DATA_FOLDER + location + "/" + date + Application.TRENDS_FILE);
                            System.out.println("Finished processing!");
                        }
                    }

                    else {
                        for (int i = 0; i < tokens.length; i++) {
                            Statistics.senseTrend(tokens[i]);
                        }
                    }
                }
            }
            catch (Exception exception) {
                System.err.println(exception.getMessage());
                continue;
            }
            System.out.print("> ");
        }
    }

}
