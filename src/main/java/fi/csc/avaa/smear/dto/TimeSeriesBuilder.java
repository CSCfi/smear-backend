package fi.csc.avaa.smear.dto;

import fi.csc.avaa.smear.constants.Aggregation;
import fi.csc.avaa.smear.constants.AggregationInterval;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.constants.Aggregation.CIRCULAR;
import static fi.csc.avaa.smear.constants.Aggregation.MEDIAN;

public class TimeSeriesBuilder {

    private final Aggregation aggregation;
    private final AggregationInterval aggregationInterval;
    private final Set<String> allColumns = new TreeSet<>();
    private final Map<String, Map<String, Double>> timeSeries = new TreeMap<>();

    public TimeSeriesBuilder(Aggregation aggregation, AggregationInterval aggregationInterval) {
        this.aggregation = aggregation;
        this.aggregationInterval = aggregationInterval;
    }

    public Map<String, Map<String, Double>> build() {
        fillNullValues();
        return timeSeries;
    }

    public void add(RowSet<Row> rowSet, String tableName, List<String> variables) {
        Map<String, String> variableToColumn = variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> String.format("%s.%s", tableName, variable)));
        allColumns.addAll(variableToColumn.values());

        if (aggregation.isGroupedManually()) {
            groupAndAddToSeries(rowSet, variableToColumn);
        } else {
            addToSeries(rowSet, variableToColumn);
        }
    }

    private void addToSeries(RowSet<Row> rowSet, Map<String, String> variableToColumn) {
        rowSet.forEach(row -> {
            String samptime = row.getLocalDateTime("samptime").toString();
            if (!timeSeries.containsKey(samptime)) {
                timeSeries.put(samptime, new TreeMap<>());
            }
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptime).put(column, row.getDouble(variable)));
        });
    }

    private void groupAndAddToSeries(RowSet<Row> rowSet, Map<String, String> variableToColumn) {
        LocalDateTime aggregateSamptime = null;
        Map<String, List<Double>> variableToValues = new HashMap<>();
        for (Row row : rowSet) {
            if (aggregateSamptime == null) {
                aggregateSamptime = roundToNearestMinute(row.getLocalDateTime("samptime"))
                        .plusMinutes(aggregationInterval.getMinutes());
            }
            LocalDateTime samptime = roundToNearestMinute(row.getLocalDateTime("samptime"));
            Iterator<Entry<String, String>> variableIterator = variableToColumn.entrySet().iterator();
            while (variableIterator.hasNext()) {
                Entry<String, String> entry = variableIterator.next();
                String variable = entry.getKey();
                String column = entry.getValue();
                if (!variableToValues.containsKey(variable)) {
                    variableToValues.put(variable, new ArrayList<>());
                }
                List<Double> values = variableToValues.get(variable);
                Double value = row.getDouble(variable);
                if (samptime.isAfter(aggregateSamptime) || samptime.isEqual(aggregateSamptime)) {
                    String key = aggregateSamptime.toString();
                    if (!timeSeries.containsKey(key)) {
                        timeSeries.put(key, new HashMap<>());
                    }
                    timeSeries.get(key).put(column, aggregateOf(values));
                    if (!variableIterator.hasNext()) {
                        aggregateSamptime = samptime.plusMinutes(aggregationInterval.getMinutes());
                    }
                    values.clear();
                }
                values.add(value);
            }
        }
    }

    private LocalDateTime roundToNearestMinute(LocalDateTime timestamp) {
        if (timestamp.getSecond() >= 30) {
            return timestamp.plusMinutes(1).withSecond(0).withNano(0);
        }
        return timestamp.withSecond(0).withNano(0);
    }

    private double aggregateOf(List<Double> values) {
        if (aggregation.equals(MEDIAN)) {
            return medianOf(values);
        } else if (aggregation.equals(CIRCULAR)) {
            return circularMeanOf(values);
        } else {
            throw new UnsupportedOperationException(
                    String.format("Aggregation not supported: %s", aggregation.name()));
        }
    }

    private double medianOf(List<Double> values) {
        Collections.sort(values);
        int noOfValues = values.size();
        if (noOfValues % 2 == 0) {
            return (values.get(noOfValues / 2) + values.get(noOfValues / 2 - 1)) / 2;
        } else {
            return values.get(noOfValues / 2);
        }
    }

    private double circularMeanOf(List<Double> values) {
        double s = 0;
        double c = 0;
        for (Double value : values) {
            c += Math.cos(Math.toRadians(value));
            s += Math.sin(Math.toRadians(value));
        }
        c = c / values.size();
        s = s / values.size();
        double sc = s / c;
        double mean = Math.toDegrees(Math.atan(sc));
        if (c < 0) {
            mean += 180;
        } else if (s < 0 && c > 0) {
            mean += 360;
        }
        return (double) Math.round(mean * 100) / 100;
    }

    private void fillNullValues() {
        timeSeries.values().forEach(columnToSampleValue ->
                allColumns.forEach(column -> {
                    if (!columnToSampleValue.containsKey(column)) {
                        columnToSampleValue.put(column, null);
                    }
                }));
    }
}
