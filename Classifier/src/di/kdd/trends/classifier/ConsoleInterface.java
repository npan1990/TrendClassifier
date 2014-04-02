package di.kdd.trends.classifier;

import di.kdd.trends.classifier.processing.TrendVector;
import di.kdd.trends.classifier.statistics.Application;
import di.kdd.trends.classifier.statistics.Statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by panos on 2/28/14.
 */

public class ConsoleInterface {

    private static String VECTORS_DIRECTORY = Application.DATA_FOLDER + "Vectors/";
    private static String VECTOR_FILE_NAME = "vector.csv";

    private static String currentLocation = null;
    private static String currentDate = null;

    private static String MEME_TAG = "m";
    private static String EVENT_TAG = "e";

    private static ArrayList<TrendVector> trendVectors = null;

    public static void main (String []args) throws Exception {

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
                else if (tokens.length == 1 && tokens[0].compareTo("ls -a") == 0) {
                    Statistics.listAll();
                }
                else if (tokens.length == 1 && tokens[0].compareTo("q") == 0) {
                    return;
                }
                else if (tokens.length == 1 && tokens[0].compareTo("tag") == 0) {
                    System.out.print("Entered tag mode\ntag-mode>");

                    ConsoleInterface.trendVectors = ConsoleInterface.loadVectors();

                    while ((command = scanner.nextLine()).compareTo("q") != 0) {
                        if (command.compareTo("ls") == 0) {
                            Statistics.list();
                        }
                        else if (command.compareTo("ls -a") == 0) {
                            Statistics.listAll();
                        }
                        else if (command.compareTo("ls -t") == 0) {
                            for (TrendVector trendVector : ConsoleInterface.trendVectors) {
                                System.out.println(trendVector.getTrend() + " " + trendVector.getTrendClass());
                            }
                        }
                        else {
                            String trend = command;

                            if (Statistics.getTrends().contains(trend) == false) {
                                System.out.println(trend + " doesn't exist in current trends");
                                Statistics.listAll();
                            }
                            else {

                                TrendVector trendVector = new TrendVector();
                                trendVector.setTrend(trend);

                                trendVector = Statistics.getTrendFeatures(trend);

                                if (ConsoleInterface.trendIsTagged(trend)) {
                                    trendVector = ConsoleInterface.getTaggedTrendVector(trend);
                                    System.out.println(trend + " is tagged as " + trendVector.getTrendClass());
                                    System.out.println("Enter new tag");
                                }

                                System.out.print(trend + " is meme (m) or event (e)?\ntag-mode>");

                                command = scanner.nextLine();

                                while (command.compareTo(ConsoleInterface.MEME_TAG) != 0 &&
                                        command.compareTo(ConsoleInterface.EVENT_TAG) != 0) {

                                    System.out.print(trend + " is meme (m) or event (e)?\ntag-mode>");
                                }



                                if (command.compareTo(ConsoleInterface.MEME_TAG) == 0) {
                                    trendVector.setTrendClass(TrendVector.TrendClass.Meme);
                                }
                                else if (command.compareTo(ConsoleInterface.EVENT_TAG) == 0 ) {
                                    trendVector.setTrendClass(TrendVector.TrendClass.Event);
                                }

                                ConsoleInterface.updateTrendVector(trendVector);
                            }
                        }

                        System.out.print("tag-mode>");
                    }

                    ConsoleInterface.dumpTrendVectors();
                    System.out.println("Exited tag mode");
                }
                else if (tokens.length > 0) {
                    String[] split = tokens[0].split(" ");

                    // Set date
                    if (split[0].compareTo("d") == 0) {
                        ConsoleInterface.currentDate = split[1];
                        System.out.println("Date: " + ConsoleInterface.currentDate);
                        if (ConsoleInterface.currentLocation == null) {
                            System.out.println("Location not set yet!");
                        }
                        else {
                            System.out.println("Location: " + ConsoleInterface.currentLocation);
                            System.out.println("Processing data...");
                            Statistics.load(Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TWEETS_FILE,
                                    Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TRENDS_FILE);
                            System.out.println("Finished processing!");
                        }
                    }

                    // Set location
                    else if (split[0].compareTo("l") == 0) {
                        ConsoleInterface.currentLocation = split[1];
                        System.out.println("Location: " + ConsoleInterface.currentLocation);
                        if (ConsoleInterface.currentDate == null) {
                            System.out.println("Date not set yet!");
                        }
                        else {
                            System.out.println("Date: " + ConsoleInterface.currentDate);
                            System.out.println("Processing data...");
                            Statistics.load(Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TWEETS_FILE,
                                    Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TRENDS_FILE);
                            System.out.println("Finished processing!");
                        }
                    }

                    else {
                        for (int i = 0; i < tokens.length; i++) {
                            TrendVector trendVector = new TrendVector();
                            trendVector.setTrend(tokens[i]);
                            Statistics.senseTrend(trendVector);
                        }
                    }
                }
            }
            catch (Exception exception) {
                System.err.println(exception.getMessage());
                exception.printStackTrace();
                continue;
            }
            System.out.print("> ");
        }
    }

    private static boolean trendIsTagged (String trend) {

        if (ConsoleInterface.trendVectors == null) {
            return false;
        }

        for (TrendVector trendVector : ConsoleInterface.trendVectors) {
            if (trendVector.getTrend().compareTo(trend) == 0) {
                return true;
            }
        }

        return false;
    }

    private static TrendVector getTaggedTrendVector (String trend) {
        if (ConsoleInterface.trendVectors == null) {
            return null;
        }

        for (int i = 0; i < ConsoleInterface.trendVectors.size(); i++) {
            if (ConsoleInterface.trendVectors.get(i).getTrend().compareTo(trend) == 0) {
                return ConsoleInterface.trendVectors.get(i);
            }
        }

        return null;
    }

    private static void updateTrendVector (TrendVector trendVector) {
        if (ConsoleInterface.trendVectors == null) {
            return;
        }

        for (int i = 0; i < ConsoleInterface.trendVectors.size(); i++) {
            if (ConsoleInterface.trendVectors.get(i).getTrend().compareTo(trendVector.getTrend()) == 0) {
                ConsoleInterface.trendVectors.remove(i);
                ConsoleInterface.trendVectors.add(i, trendVector);

                return;
            }
        }

        ConsoleInterface.trendVectors.add(trendVector);
    }

    private static String getCurrentDirectory() {
        return ConsoleInterface.VECTORS_DIRECTORY + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + "/";
    }

    private static String getCurrentVectorFileName () {
        return ConsoleInterface.getCurrentDirectory() + ConsoleInterface.VECTOR_FILE_NAME;
    }

    private static void setupFileSystem() throws Exception {
        File locationDir = new File(ConsoleInterface.getCurrentDirectory());

        if (locationDir.exists() == false) {
            locationDir.mkdirs();
            (new File(ConsoleInterface.getCurrentDirectory() + ConsoleInterface.VECTOR_FILE_NAME)).createNewFile();
        }
    }


    private static ArrayList<TrendVector> loadVectors() throws Exception {
        ArrayList<TrendVector> trendVectors = new ArrayList<TrendVector>();

        ConsoleInterface.setupFileSystem();

        BufferedReader reader = new BufferedReader(new FileReader(ConsoleInterface.getCurrentDirectory() + ConsoleInterface.VECTOR_FILE_NAME));

        // Skip first line
        String line = reader.readLine();

        Statistics.printToConsole = false;
        while ((line = reader.readLine()) != null) {
            TrendVector trendVector = new TrendVector(line);
            trendVector = Statistics.getTrendFeatures(trendVector.getTrend(), trendVector.getTrendClass());
            trendVectors.add(trendVector);
        }
        Statistics.printToConsole = true;
        return trendVectors;
    }

    private static void dumpTrendVectors() throws Exception {
        (new File(ConsoleInterface.getCurrentVectorFileName())).delete();
        (new File(ConsoleInterface.getCurrentVectorFileName())).createNewFile();

        PrintWriter vectorWriter = new PrintWriter(new File(ConsoleInterface.getCurrentVectorFileName()));

        vectorWriter.println(TrendVector.getColumnNames());
        for (TrendVector trendVector : ConsoleInterface.trendVectors) {
            vectorWriter.println(trendVector.toCsv());
        }

        vectorWriter.flush();
        vectorWriter.close();
    }
}
