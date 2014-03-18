package di.kdd.trends.classifier.processing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by dimkots on 3/18/14.
 */
public class DateWeightPair {
    private DateRange dateRange;
    private int weight;

    public DateWeightPair(DateRange dateRange, int weight) {
        this.dateRange = dateRange;
        this.weight = weight;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public DateRange getDateRange() {

        return dateRange;
    }

    public int getWeight() {
        return weight;
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
