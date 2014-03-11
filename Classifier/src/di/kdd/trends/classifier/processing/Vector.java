package di.kdd.trends.classifier.processing;

/**
 * Created by panos on 3/11/14.
 */

public class Vector {

    enum TrendClass { Meme, PlannedEvent, UnplannedEvent, General };

    private String trend;
    private TrendClass trendClass;

    public Vector (String trend, TrendClass trendClass) {
        this.trend = trend;
        this.trendClass = trendClass;
    }

    public String getTrend() { return this.trend; }

    public TrendClass getTrendClass() { return this.trendClass; }

}
