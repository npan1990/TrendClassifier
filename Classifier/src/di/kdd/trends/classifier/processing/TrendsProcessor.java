package di.kdd.trends.classifier.processing;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by panossakkos on 2/19/14.
 */

public class TrendsProcessor {

    private Hashtable<String, ArrayList<TrendValue>> trends = new Hashtable<String, ArrayList<TrendValue>>();

    public void process(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String dateLine;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        ArrayList<String> currentTrends = new ArrayList<String>();
        ArrayList<String> previousTrends = new ArrayList<String>();

        while ((dateLine = reader.readLine()) != null) {
            Date date = dateFormat.parse(dateLine);

            String trend;
            currentTrends.clear();

            for (int rank  = 1; rank <= 10; rank++) {
                trend = reader.readLine().toLowerCase().replace("#", "");

                if (this.trends.containsKey(trend) == false) {
                    this.trends.put(trend, new ArrayList<TrendValue>());
                }

                ArrayList<TrendValue> trendValues = this.trends.get(trend);

                if (previousTrends.contains(trend)) {

                    /* If trend existed in the last crawl, update its last TrendValue. */

                    if (trendValues.size() == 0) {

                        /* First occurrence */

                        trendValues.add(new TrendValue(date, rank));
                    }
                    else {
                        trendValues.get(trendValues.size() - 1).update(date, rank);
                    }
                }
                else {

                    /* Otherwise, add a new TrendValue */

                    trendValues.add(new TrendValue(date, rank));
                }

                currentTrends.add(trend);
            }

            previousTrends.clear();
            previousTrends.addAll(currentTrends);
        }
    }

    public int getMaxRank(String trend) {
        int maxRank = 0;

        for (TrendValue value : this.trends.get(trend)) {
            if (value.getMaximumRank() > maxRank) {
                maxRank = value.getMaximumRank();
            }
        }

        return maxRank;
    }

    public int getDuration(String trend) {
        int duration = 0;

        for (TrendValue value : this.trends.get(trend)) {
            duration += value.getRanking().size();
        }

        return duration;
    }

    public ArrayList<String> getTrends() {
        ArrayList<String> trends = new ArrayList<String>();

        for (String trend : this.trends.keySet()) {
            trends.add(trend);
        }

        return trends;
    }

    void dumpTrends(String where) throws Exception {
        PrintWriter trendsWriter = new PrintWriter(new BufferedWriter(new FileWriter(where, true)));

        for (String trend : this.trends.keySet()) {
            trendsWriter.println(trend);

            for (TrendValue trendValue : this.trends.get(trend)) {
                trendsWriter.println("From: " + trendValue.getDateRange().getFrom() +
                                    " To : " + trendValue.getDateRange().getTo());

                for (Integer rank : trendValue.getRanking()) {
                    trendsWriter.print(rank + " ");
                }

                trendsWriter.println();
            }
        }

        trendsWriter.flush();
        trendsWriter.close();
    }

    public void dump(String trend) {
        System.out.println();

        for (TrendValue value : this.trends.get(trend)) {
            System.out.println(value.toString());
        }
    }
}
