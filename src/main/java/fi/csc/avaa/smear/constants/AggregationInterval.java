package fi.csc.avaa.smear.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AggregationInterval {

    INTERVAL_30MIN("30MIN", 30),
    INTERVAL_60MIN("60MIN", 60);

    private final String queryParam;
    private final int minutes;

    private static final List<String> queryParams = new ArrayList<>();
    private static final Map<String, AggregationInterval> lookup = new HashMap<>();

    static {
        for (AggregationInterval interval : values()) {
            queryParams.add(interval.queryParam);
            lookup.put(interval.queryParam, interval);
        }
    }

    AggregationInterval(String queryParam, int minutes) {
        this.queryParam = queryParam;
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public static List<String> getQueryParams() {
        return queryParams;
    }

    public static AggregationInterval from(String queryParam) {
        return lookup.get(queryParam.toUpperCase());
    }
}
