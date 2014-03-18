package di.kdd.trends.classifier.processing;

import java.io.StringWriter;
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
