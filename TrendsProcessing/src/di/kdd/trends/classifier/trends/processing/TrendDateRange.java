package di.kdd.trends.classifier.trends.processing;

import java.util.Date;

/**
 * Created by panossakkos on 2/19/14.
 */

public class TrendDateRange {

    private Date from, to;

    public TrendDateRange (Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public Date getFrom () {
        return this.from;
    }

    public Date getTo () {
        return this.to;
    }

    public boolean inRange (Date date) {
        return (from.before(date) && to.after(date));
    }

    public void updateRange (Date date) {
        if (from.before(date) == false) {
            this.from = date;
        }
        else if (to.after(date) == false) {
            this.to = date;
        }
    }
}
