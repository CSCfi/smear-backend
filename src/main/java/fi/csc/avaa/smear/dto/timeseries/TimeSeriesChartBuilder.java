package fi.csc.avaa.smear.dto.timeseries;

import fi.csc.avaa.smear.parameter.Aggregation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimeSeriesChartBuilder extends TimeSeriesBuilder<Map<String, List<List<Number>>>> {

    private final Map<String, List<List<Number>>> variableToSeries = new TreeMap<>();

    public TimeSeriesChartBuilder(LocalDateTime startTime, LocalDateTime endTime,
                                  Aggregation aggregation, Integer aggregationInterval) {
        super(startTime, endTime, aggregation, aggregationInterval);
    }

    @Override
    protected void addDataPoint(LocalDateTime samptime, String column, Number value) {
        if (value != null) {
            initSeries(column);
            long samptimeMilli = samptime.toInstant(ZoneOffset.UTC).toEpochMilli();
            List<Number> dataPoint = Arrays.asList(samptimeMilli, value);
            variableToSeries.get(column).add(dataPoint);
        }
    }

    @Override
    public Map<String, List<List<Number>>> build() {
        return variableToSeries;
    }

    private void initSeries(String column) {
        if (!variableToSeries.containsKey(column)) {
            variableToSeries.put(column, new ArrayList<>());
        }
    }
}
