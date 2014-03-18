package di.kdd.trends.classifier.processing;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by panossakkos on 2/19/14.
 */

public class TrendValue {

    private DateRange dateRange = new DateRange();
    private ArrayList<Integer> rankings = new ArrayList<Integer>();

    public TrendValue (Date date, int ranking) {
        this.update(date, ranking);
    }

    public void update (Date date, int ranking) {
        this.dateRange.updateRange(date);
        this.rankings.add(ranking);
    }

    public DateRange getDateRange() {
        return this.dateRange;
    }

    public ArrayList<Integer> getRanking()
    {
        return this.rankings;
    }

    public int getMaximumRank()
    {
        int maximumRank = 0;

        for (Integer integer : this.rankings) {
            if (integer > maximumRank) {
                maximumRank = integer;
            }
        }

        return maximumRank;
    }

    public boolean isInSlice(int start, int end) throws Exception {
        DateFormat hourFormat = new SimpleDateFormat("HH");

        String startHourString = hourFormat.parse(hourFormat.format(this.dateRange.getFrom())).toString().replaceFirst("0", "");
        String endHourString = hourFormat.parse(hourFormat.format(this.dateRange.getTo())).toString().replaceFirst("0", "");

        int hourIndex = startHourString.indexOf(":") - 2;
        startHourString = startHourString.substring(hourIndex, hourIndex + 2).replace("0", "");
        hourIndex = endHourString.indexOf(":") - 2;
        endHourString = endHourString.substring(hourIndex, hourIndex + 2).replace("0", "");

        int startHour;

        if (startHourString.isEmpty()) {
            startHour = 0;
        }
        else {
            startHour = Integer.parseInt(startHourString);
        }

        int endHour;

        if (endHourString.isEmpty()) {
            endHour = 0;
        }
        else {
            endHour = Integer.parseInt(endHourString);
        }

        if (startHour < start && endHour < start ||
                startHour > end && endHour > end) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringWriter output = new StringWriter();

        output.append(this.dateRange.toString() + "\n");

        for (Integer rank : this.rankings) {
            output.append(rank + " ");
        }
        output.append("\n");

        return output.toString();
    }
}
