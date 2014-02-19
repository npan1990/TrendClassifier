package di.kdd.trends.classifier.trends.processing;

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
}
