package fi.csc.avaa.smear.dto.timeseries;

import fi.csc.avaa.smear.parameter.Aggregation;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.SQLDataType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.dto.timeseries.TimeSeriesUtil.aggregateOf;
import static fi.csc.avaa.smear.dto.timeseries.TimeSeriesUtil.columnNameFor;
import static fi.csc.avaa.smear.dto.timeseries.TimeSeriesUtil.mapVariablesToColumnNames;
import static fi.csc.avaa.smear.dto.timeseries.TimeSeriesUtil.roundToNearestMinute;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COLNAME_CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.SAMPTIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.START_TIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_TREE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VALUE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VARIABLE;
import static org.jooq.impl.DSL.field;

public abstract class TimeSeriesBuilder<T> {

    protected final LocalDateTime startTime;
    protected final LocalDateTime endTime;
    protected final Aggregation aggregation;
    protected final Integer aggregationInterval;

    public abstract T build();
    protected abstract void addDataPoint(LocalDateTime samptime, String column, Number value);

    public TimeSeriesBuilder(LocalDateTime startTime, LocalDateTime endTime,
                             Aggregation aggregation, Integer aggregationInterval) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.aggregation = aggregation;
        this.aggregationInterval = aggregationInterval;
    }

    public void addResult(Result<Record> result, String tableName, List<String> variables) {
        Map<String, String> variableToColumnName = mapVariablesToColumnNames(tableName, variables);
        if (aggregation.isGroupedManually()) {
            groupAndAddToSeries(result, variableToColumnName);
        } else {
            addToSeries(result, variableToColumnName);
        }
    }

    public void addHyySlowResult(Result<Record3<LocalDateTime, String, Double>> result) {
        result.forEach(record -> {
            LocalDateTime samptime = record.get(START_TIME);
            String variable = record.get(VARIABLE);
            String column = columnNameFor(TABLENAME_HYY_SLOW, variable);
            Double value = record.get(VALUE);
            addDataPoint(samptime, column, value);
        });
    }

    public void addHyyTreeResult(Result<Record> result, List<String> variables) {
        Map<String, String> variableToColumn = mapVariablesToColumnNames(TABLENAME_HYY_TREE, variables);
        result.forEach(record -> {
            LocalDateTime samptime = record.get(SAMPTIME);
            String cuvNoColumn = columnNameFor(TABLENAME_HYY_TREE, COLNAME_CUV_NO);
            Integer cuvNoValue = record.get(CUV_NO);
            addDataPoint(samptime, cuvNoColumn, cuvNoValue);
            variableToColumn.forEach((variable, column) -> {
                Number value = record.get(field(variable, SQLDataType.NUMERIC));
                addDataPoint(samptime, column, value);
            });
        });
    }

    private void groupAndAddToSeries(Result<Record> result, Map<String, String> variableToColumn) {
        LocalDateTime aggregateSamptime = null;
        Map<String, List<Double>> variableToValues = new HashMap<>();
        for (Record record : result) {
            LocalDateTime samptime = roundToNearestMinute(record.get(SAMPTIME, LocalDateTime.class));
            if (aggregateSamptime == null) {
                aggregateSamptime = samptime.plusMinutes(aggregationInterval);
            }
            Iterator<Map.Entry<String, String>> variableIterator = variableToColumn.entrySet().iterator();
            while (variableIterator.hasNext()) {
                Map.Entry<String, String> entry = variableIterator.next();
                String variable = entry.getKey();
                if (!variableToValues.containsKey(variable)) {
                    variableToValues.put(variable, new ArrayList<>());
                }
                List<Double> values = variableToValues.get(variable);
                Double value = record.get(field(variable), Double.class);
                if (samptime.isAfter(aggregateSamptime) || samptime.isEqual(aggregateSamptime)) {
                    String column = entry.getValue();
                    Double aggregate = aggregateOf(values, aggregation);
                    addDataPoint(aggregateSamptime, column, aggregate);
                    if (!variableIterator.hasNext()) {
                        aggregateSamptime = samptime.plusMinutes(aggregationInterval);
                    }
                    values.clear();
                }
                values.add(value);
            }
        }
    }

    private void addToSeries(Result<Record> result, Map<String, String> variableToColumn) {
        result.forEach(record -> {
            LocalDateTime samptime = record.get(SAMPTIME);
            variableToColumn.forEach((variable, column) -> {
                Number value = record.get(field(variable, SQLDataType.NUMERIC));
                addDataPoint(samptime, column, value);
            });
        });
    }
}
