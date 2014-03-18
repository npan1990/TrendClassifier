package di.kdd.trends.classifier.processing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by dimkots on 3/18/14.
 */
public class DateRankPair {
    private DateRange dateRange;
    private int rank;

    public DateRankPair(DateRange dateRange, int rank) {
        this.dateRange = dateRange;
        this.rank = rank;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public DateRange getDateRange() {

        return dateRange;
    }

    public int getRank() {
        return rank;
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
}
