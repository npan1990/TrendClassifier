package di.kdd.trends.classifier;

import di.kdd.trends.classifier.processing.TrendVector;
import di.kdd.trends.classifier.statistics.Application;
import di.kdd.trends.classifier.statistics.Statistics;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by panos on 2/28/14.
 */

public class ConsoleInterface {

    public static String VECTORS_DIRECTORY = Application.DATA_FOLDER + "Vectors/";
    public static String VECTOR_FILE_NAME = "vector.csv";

    public static String currentLocation = null;
    public static String currentDate = null;

    private static String MEME_TAG = "m";
    private static String EVENT_TAG = "e";
    private static String QUIT = "q";
    private static String UPDATE_VECTORS_CMD = "update";

    private static ArrayList<TrendVector> trendVectors = null;

    public static void main (String []args) throws Exception {

        String command;
        Scanner scanner = new Scanner(System.in);

        System.out.print("> ");
        while ((command = scanner.nextLine()) != null) {
            try {
                String tokens[] = command.split(",");

                if (tokens.length == 1 && tokens[0].startsWith("#")) {
                    String[] fields = tokens[0].split(" ");
                    Statistics.hashtags(Integer.parseInt(fields[1]));
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
                else if (command.compareTo(ConsoleInterface.UPDATE_VECTORS_CMD) == 0) {
                    ConsoleInterface.updateVectors();
                }
                else if (tokens.length == 1 && tokens[0].compareTo("tag") == 0) {
                    System.out.print("Entered tag mode\ntag-mode>");

                    ConsoleInterface.trendVectors = ConsoleInterface.loadVectors();

                    while ((command = scanner.nextLine()).compareTo("q") != 0) {
                        if (command.compareTo("ls") == 0) {
                            Statistics.list();
                        }
                        else if (command.startsWith("#")) {
                            String[] fields = command.split(" ");
                            Statistics.hashtags(Integer.parseInt(fields[1]));
                        }
                        else if (command.compareTo("ls -a") == 0) {
                            Statistics.listAll();
                        }
                        else if (command.compareTo("ls -t") == 0) {
                            for (TrendVector trendVector : ConsoleInterface.trendVectors) {
                                System.out.println(trendVector.getTrend() + " " + trendVector.getTrendClass());
                            }
                        }
                        else if (command.compareTo("ls -u") == 0) {
                            Statistics.filterTrends();

                            for (String trend : Statistics.cachedTrends) {
                                boolean isTagged = false;

                                for (TrendVector vector : ConsoleInterface.trendVectors) {
                                    if (vector.getTrend().compareTo(trend) == 0) {
                                        isTagged = true;
                                        break;
                                    }
                                }

                                if (!isTagged) {
                                    System.out.println(trend);
                                }
                            }
                        }
                        else {
                            String trend = command;

                            TrendVector trendVector = new TrendVector();
                            trendVector.setTrend(trend);

                            trendVector = Statistics.getTrendFeatures(trend);

                            if (ConsoleInterface.trendIsTagged(trend)) {
                                trendVector = ConsoleInterface.getTaggedTrendVector(trend);
                                System.out.println(trend + " is tagged as " + trendVector.getTrendClass());
                                System.out.println("Enter new tag");
                            }

                            System.out.print(trend + " is meme (m) or event (e)? ('q' for quit)\ntag-mode>");

                            command = scanner.nextLine();

                            while (command.compareTo(ConsoleInterface.MEME_TAG) != 0 &&
                                    command.compareTo(ConsoleInterface.EVENT_TAG) != 0 &&
                                    command.compareTo(ConsoleInterface.QUIT) != 0) {

                                System.out.print(trend + " is meme (m) or event (e)? ('q' for quit)\ntag-mode>");
                                command = scanner.nextLine();
                            }

                            if (command.compareTo(ConsoleInterface.MEME_TAG) == 0) {
                                trendVector.setTrendClass(TrendVector.TrendClass.Meme);
                                ConsoleInterface.updateTrendVector(trendVector);
                            }
                            else if (command.compareTo(ConsoleInterface.EVENT_TAG) == 0 ) {
                                trendVector.setTrendClass(TrendVector.TrendClass.Event);
                                ConsoleInterface.updateTrendVector(trendVector);
                            }
                            else if (command.compareTo(ConsoleInterface.QUIT) == 0) {
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
                            Statistics.load(Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TWEETS_FILE);
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
                            Statistics.load(Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TWEETS_FILE);
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


    public static ArrayList<TrendVector> loadVectors() throws Exception {
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

    public static void dumpTrendVectors() throws Exception {

        System.out.println("Writing vectors to disk");

        (new File(ConsoleInterface.getCurrentVectorFileName())).delete();
        (new File(ConsoleInterface.getCurrentVectorFileName())).createNewFile();

        PrintWriter vectorWriter = new PrintWriter(new File(ConsoleInterface.getCurrentVectorFileName()));

        vectorWriter.println(TrendVector.getColumnNames());
        for (TrendVector trendVector : ConsoleInterface.trendVectors) {
            if (trendVector.getClass() != null) {
                vectorWriter.println(trendVector.toCsv());
            }
        }

        vectorWriter.flush();
        vectorWriter.close();
    }

    public static void updateVectors() throws Exception {

        /* For each month under the Vectors directory */

        File vectorsDirectory = new File(ConsoleInterface.VECTORS_DIRECTORY);

        String []months = vectorsDirectory.list();

        /* For each month */

        for (String month : months) {

            File monthVectors = new File(vectorsDirectory + "/" + month);

            if (monthVectors.isDirectory()) {

                ConsoleInterface.currentLocation = month;

                /* For each day */

                String []days = monthVectors.list();

                for (String day : days) {

                    ConsoleInterface.currentDate = day;

                    System.out.println("Date: " + ConsoleInterface.currentDate);
                    System.out.println("Location: " + ConsoleInterface.currentLocation);
                    System.out.println("Processing data...");
                    Statistics.load(Application.DATA_FOLDER + ConsoleInterface.currentLocation + "/" + ConsoleInterface.currentDate + Application.TWEETS_FILE);
                    System.out.println("Finished processing!");

                    ConsoleInterface.trendVectors = ConsoleInterface.loadVectors();
                    ConsoleInterface.dumpTrendVectors();
                }
            }
        }
    }
}
