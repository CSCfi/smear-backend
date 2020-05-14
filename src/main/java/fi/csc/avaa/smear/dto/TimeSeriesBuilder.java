package fi.csc.avaa.smear.dto;

import fi.csc.avaa.smear.constants.Aggregation;
import fi.csc.avaa.smear.constants.AggregationInterval;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;

import java.sql.Timestamp;
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
import static org.jooq.impl.DSL.field;

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

    public void addResult(Result<Record> result, String tableName, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(tableName, variables);
        allColumns.addAll(variableToColumn.values());
        if (aggregation.isGroupedManually()) {
            groupAndAddToSeries(result, variableToColumn);
        } else {
            addToSeries(result, variableToColumn);
        }
    }

    public void addHyySlowResult(Result<Record3<Timestamp, String, Double>> result) {
        result.forEach(record -> {
            String variable = record.get(field(COL_VARIABLE), String.class);
            String column = getColName(TABLE_HYY_SLOW, variable);
            allColumns.add(column);
            String samptimeStr = ISO_DATE_TIME.format(record.get(field(COL_START_TIME), LocalDateTime.class));
            initSamptime(samptimeStr);
            timeSeries.get(samptimeStr).put(column, record.get(field(COL_VALUE), Double.class));
        });
    }

    public void addHyyTreeResult(Result<Record> result, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(TABLE_HYY_TREE, variables);
        allColumns.addAll(variableToColumn.values());
        result.forEach(record -> {
            String samptimeStr = ISO_DATE_TIME.format(record.get(field(COL_SAMPTIME), LocalDateTime.class));
            initSamptime(samptimeStr);
            timeSeries.get(samptimeStr)
                    .put(getColName(TABLE_HYY_TREE, COL_CUV_NO), record.get(field(COL_CUV_NO), Integer.class));
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptimeStr).put(column, record.get(field(variable), Double.class)));
        });
    }

    private void addToSeries(Result<Record> result, Map<String, String> variableToColumn) {
        result.forEach(record -> {
            String samptimeStr = ISO_DATE_TIME.format(record.get(field(COL_SAMPTIME), LocalDateTime.class));
            initSamptime(samptimeStr);
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptimeStr).put(column, record.get(field(variable))));
        });
    }

    private Map<String, String> mapVariablesToColumns(String tableName, List<String> variables) {
        return variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> getColName(tableName, variable)));
    }

    private void groupAndAddToSeries(Result<Record> result, Map<String, String> variableToColumn) {
        LocalDateTime aggregateSamptime = null;
        Map<String, List<Double>> variableToValues = new HashMap<>();
        for (Record record : result) {
            LocalDateTime samptime = roundToNearestMinute(record.get(field(COL_SAMPTIME), LocalDateTime.class));
            if (aggregateSamptime == null) {
                aggregateSamptime = samptime.plusMinutes(aggregationInterval.getMinutes());
            }
            Iterator<Entry<String, String>> variableIterator = variableToColumn.entrySet().iterator();
            while (variableIterator.hasNext()) {
                Entry<String, String> entry = variableIterator.next();
                String variable = entry.getKey();
                String column = entry.getValue();
                if (!variableToValues.containsKey(variable)) {
                    variableToValues.put(variable, new ArrayList<>());
                }
                List<Double> values = variableToValues.get(variable);
                Double value = record.get(field(variable), Double.class);
                if (samptime.isAfter(aggregateSamptime) || samptime.isEqual(aggregateSamptime)) {
                    String samptimeStr = ISO_DATE_TIME.format(aggregateSamptime);
                    initSamptime(samptimeStr);
                    timeSeries.get(samptimeStr).put(column, aggregateOf(values));
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
