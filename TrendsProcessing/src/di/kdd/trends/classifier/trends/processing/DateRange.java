package di.kdd.trends.classifier.trends.processing;

import java.util.Date;

/**
 * Created by panossakkos on 2/19/14.
 */

public class DateRange {

    private Date from, to;

    public Date getFrom () {
        return this.from;
    }

    public Date getTo () {
        return this.to;
    }

    public void updateRange (Date date) {
        if (from == null || from.before(date) == false) {
            this.from = date;
        }
        else if (to == null || to.after(date) == false) {
            this.to = date;
        }
    }
}
