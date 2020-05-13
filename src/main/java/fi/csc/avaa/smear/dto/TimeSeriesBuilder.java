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
import static fi.csc.avaa.smear.constants.DBConstants.COL_CUV_NO;
import static fi.csc.avaa.smear.constants.DBConstants.COL_SAMPTIME;
import static fi.csc.avaa.smear.constants.DBConstants.COL_START_TIME;
import static fi.csc.avaa.smear.constants.DBConstants.COL_VALUE;
import static fi.csc.avaa.smear.constants.DBConstants.COL_VARIABLE;
import static fi.csc.avaa.smear.constants.DBConstants.TABLE_HYY_SLOW;
import static fi.csc.avaa.smear.constants.DBConstants.TABLE_HYY_TREE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TimeSeriesBuilder {

    private final Aggregation aggregation;
    private final AggregationInterval aggregationInterval;
    private final Set<String> allColumns = new TreeSet<>();
    private final Map<String, Map<String, Object>> timeSeries = new TreeMap<>();

    public TimeSeriesBuilder(Aggregation aggregation, AggregationInterval aggregationInterval) {
        this.aggregation = aggregation;
        this.aggregationInterval = aggregationInterval;
    }

    public Map<String, Map<String, Object>> build() {
        fillNullValues();
        return timeSeries;
    }

    public void addRowSet(RowSet<Row> rowSet, String tableName, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(tableName, variables);
        allColumns.addAll(variableToColumn.values());
        if (aggregation.isGroupedManually()) {
            groupAndAddToSeries(rowSet, variableToColumn);
        } else {
            addToSeries(rowSet, variableToColumn);
        }
    }

    public void addHyySlowRowSet(RowSet<Row> rowSet) {
        rowSet.forEach(row -> {
            String variable = row.getString(COL_VARIABLE);
            String column = getColName(TABLE_HYY_SLOW, variable);
            allColumns.add(column);
            String samptimeStr = ISO_DATE_TIME.format(row.getLocalDateTime(COL_START_TIME));
            initSamptime(samptimeStr);
            timeSeries.get(samptimeStr).put(column, row.getDouble(COL_VALUE));
        });
    }

    public void addHyyTreeRowSet(RowSet<Row> rowSet, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(TABLE_HYY_TREE, variables);
        allColumns.addAll(variableToColumn.values());
        rowSet.forEach(row -> {
            String samptimeStr = ISO_DATE_TIME.format(row.getLocalDateTime(COL_SAMPTIME));
            initSamptime(samptimeStr);
            timeSeries.get(samptimeStr).put(getColName(TABLE_HYY_TREE, COL_CUV_NO), row.getInteger(COL_CUV_NO));
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptimeStr).put(column, row.getDouble(variable)));
        });
    }

    private void addToSeries(RowSet<Row> rowSet, Map<String, String> variableToColumn) {
        rowSet.forEach(row -> {
            String samptimeStr = ISO_DATE_TIME.format(row.getLocalDateTime(COL_SAMPTIME));
            initSamptime(samptimeStr);
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptimeStr).put(column, row.getDouble(variable)));
        });
    }

    private Map<String, String> mapVariablesToColumns(String tableName, List<String> variables) {
        return variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> getColName(tableName, variable)));
    }

    private void groupAndAddToSeries(RowSet<Row> rowSet, Map<String, String> variableToColumn) {
        LocalDateTime aggregateSamptime = null;
        Map<String, List<Double>> variableToValues = new HashMap<>();
        for (Row row : rowSet) {
            if (aggregateSamptime == null) {
                aggregateSamptime = roundToNearestMinute(row.getLocalDateTime(COL_SAMPTIME))
                        .plusMinutes(aggregationInterval.getMinutes());
            }
            LocalDateTime samptime = roundToNearestMinute(row.getLocalDateTime(COL_SAMPTIME));
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
                    initSamptime(key);
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

    private String getColName(String tableName, String variable) {
        return String.format("%s.%s", tableName, variable);
    }

    private void initSamptime(String samptime) {
        if (!timeSeries.containsKey(samptime)) {
            timeSeries.put(samptime, new TreeMap<>());
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
