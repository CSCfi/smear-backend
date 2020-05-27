package fi.csc.avaa.smear.dto;

import fi.csc.avaa.smear.parameter.Aggregation;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_FORMATTER;
import static fi.csc.avaa.smear.parameter.Aggregation.CIRCULAR;
import static fi.csc.avaa.smear.parameter.Aggregation.MEDIAN;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COLNAME_CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COLNAME_SAMPTIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.SAMPTIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.START_TIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_TREE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VALUE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VARIABLE;
import static org.jooq.impl.DSL.field;

public class TimeSeriesBuilder {

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Aggregation aggregation;
    private final Integer aggregationInterval;
    private final Set<String> allColumns = new HashSet<>();
    private final Map<String, Map<String, Object>> timeSeries = new TreeMap<>();

    public TimeSeriesBuilder(LocalDateTime startTime, LocalDateTime endTime,
                             Aggregation aggregation, Integer aggregationInterval) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.aggregation = aggregation;
        this.aggregationInterval = aggregationInterval;
    }

    public TimeSeries build() {
        List<String> columns = allColumns
                .stream()
                .sorted()
                .collect(Collectors.toList());
        List<Map<String, Object>> data = timeSeries.entrySet()
                .stream()
                .map(toRow)
                .collect(Collectors.toList());
        return TimeSeries.builder()
                .startTime(startTime)
                .endTime(endTime)
                .columns(columns)
                .recordCount(data.size())
                .aggregation(aggregation.name())
                .aggregationInterval(aggregation.equals(Aggregation.NONE)
                        ? null
                        : aggregationInterval)
                .data(data)
                .build();
    }

    private final Function<Entry<String, Map<String, Object>>, Map<String, Object>> toRow = entry -> {
        Map<String, Object> row = new HashMap<>();
        row.put(COLNAME_SAMPTIME, entry.getKey());
        Map<String, Object> values = entry.getValue();
        allColumns.forEach(column -> row.put(column, values.get(column)));
        return row;
    };

    public void addResult(Result<Record> result, String tableName, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(tableName, variables);
        allColumns.addAll(variableToColumn.values());
        if (aggregation.isGroupedManually()) {
            groupAndAddToSeries(result, variableToColumn);
        } else {
            addToSeries(result, variableToColumn);
        }
    }

    public void addHyySlowResult(Result<Record3<LocalDateTime, String, Double>> result) {
        result.forEach(record -> {
            String variable = record.get(VARIABLE);
            String column = getColName(TABLENAME_HYY_SLOW, variable);
            allColumns.add(column);
            String samptimeStr = ISO8601_DATETIME_FORMATTER.format(record.get(START_TIME));
            initSamptime(samptimeStr);
            timeSeries.get(samptimeStr).put(column, record.get(VALUE));
        });
    }

    public void addHyyTreeResult(Result<Record> result, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumns(TABLENAME_HYY_TREE, variables);
        allColumns.addAll(variableToColumn.values());
        result.forEach(record -> {
            String samptimeStr = ISO8601_DATETIME_FORMATTER.format(record.get(SAMPTIME));
            initSamptime(samptimeStr);
            String cuvNoColumn = getColName(TABLENAME_HYY_TREE, COLNAME_CUV_NO);
            allColumns.add(cuvNoColumn);
            timeSeries.get(samptimeStr).put(cuvNoColumn, record.get(CUV_NO));
            variableToColumn.forEach((variable, column) ->
                    timeSeries.get(samptimeStr).put(column, record.get(field(variable), Double.class)));
        });
    }

    private void addToSeries(Result<Record> result, Map<String, String> variableToColumn) {
        result.forEach(record -> {
            String samptimeStr = ISO8601_DATETIME_FORMATTER.format(record.get(SAMPTIME));
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
            LocalDateTime samptime = roundToNearestMinute(record.get(SAMPTIME, LocalDateTime.class));
            if (aggregateSamptime == null) {
                aggregateSamptime = samptime.plusMinutes(aggregationInterval);
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
                    String samptimeStr = ISO8601_DATETIME_FORMATTER.format(aggregateSamptime);
                    initSamptime(samptimeStr);
                    timeSeries.get(samptimeStr).put(column, aggregateOf(values));
                    if (!variableIterator.hasNext()) {
                        aggregateSamptime = samptime.plusMinutes(aggregationInterval);
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
            timeSeries.put(samptime, new HashMap<>());
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
}
