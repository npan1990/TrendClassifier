package di.kdd.trends.classifier.processing;

import java.io.StringWriter;
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

        if (to == null || to.after(date) == false) {
            this.to = date;
        }
    }

    @Override
    public String toString() {
        StringWriter output = new StringWriter();

        output.append(this.from + " - " + this.to);

        return output.toString();
    }
}
