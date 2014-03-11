package di.kdd.trends.classifier;

import di.kdd.trends.classifier.processing.Vector;
import di.kdd.trends.classifier.statistics.Application;
import di.kdd.trends.classifier.statistics.Statistics;

import java.util.Scanner;

/**
 * Created by panos on 2/28/14.
 */

public class ConsoleInterface {

    private static String vectorDirectory = Application.DATA_FOLDER + "Vectors";
    private static String vectorFileName = "vector.csv";

    private static String memeTag = "m";
    private static String plannedEventTag = "p";
    private static String unplannedEventTag = "u";
    private static String generalTag = "g";

    public static void main (String []args) throws Exception {
        String date = null;
        String location = null;

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
                else if (tokens.length == 1 && tokens[0].compareTo("tag") == 0) {
                    System.out.println("Entered tag mode");

                    while ((command = scanner.nextLine()).compareTo("q") != 0) {
                        if (command.compareTo("ls") == 0) {
                            Statistics.list();
                        }
                        else {
                            String trend = command;

                            if (Statistics.getTrends().contains(trend) == false) {
                                System.out.println(trend + " doesn't exist in current trends");
                                Statistics.list();

                                continue;
                            }
                            else {
                                System.out.println(trend + " is meme (m), planned event (p), unplanned event (u) or general (g)?");

                                command = scanner.nextLine();

                                while (command.compareTo(ConsoleInterface.memeTag) != 0 &&
                                        command.compareTo(ConsoleInterface.plannedEventTag) != 0 &&
                                        command.compareTo(ConsoleInterface.unplannedEventTag) != 0 &&
                                        command.compareTo(ConsoleInterface.generalTag) != 0) {

                                    System.out.println(trend + " is meme (m), planned event (p), unplanned event (u) or general (g)?");
                                }

                                if (command.compareTo(ConsoleInterface.memeTag) == 0) {

                                }
                                else if (command.compareTo(ConsoleInterface.plannedEventTag) == 0 ) {

                                }
                                else if (command.compareTo(ConsoleInterface.unplannedEventTag) == 0) {

                                }
                                else if (command.compareTo(ConsoleInterface.generalTag) == 0) {

                                }
                            }
                        }
                    }

                    System.out.println("Exited tag mode");
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
