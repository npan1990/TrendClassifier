package di.kdd.trends.classifier.processing;

/**
 * Created by panos on 3/11/14.
 */

public class TrendVector {

    public enum TrendClass { Meme, PlannedEvent, UnplannedEvent, General };

    private static String VALUE_SEPARATOR = ", ";
    private static int TREND_INDEX = 0;
    private static int CLASS_INDEX = 1;

    private String trend;
    private TrendClass trendClass;

    public TrendVector() { }

    public TrendVector(String csvString) {

        String []values = csvString.split(TrendVector.VALUE_SEPARATOR);

        this.trend = values[TrendVector.TREND_INDEX];
        this.trendClass = TrendClass.valueOf(values[TrendVector.CLASS_INDEX]);
    }

    public TrendVector(String trend, TrendClass trendClass) {
        this.trend = trend;
        this.trendClass = trendClass;
    }

    public String getTrend () { return this.trend; }

    public void setTrend (String trend) {
        this.trend = trend;
    }

    public TrendClass getTrendClass () { return this.trendClass; }

    public void setTrendClass (TrendClass trendClass) { this.trendClass = trendClass; }

    public String toCsv () {
        return this.trend
                + TrendVector.VALUE_SEPARATOR
                + this.trendClass;
    }
}
