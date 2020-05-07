package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TimeSeriesBuilder {

    private final Set<String> allColumns = new TreeSet<>();
    private final Map<String, Map<String, Double>> timeSeries = new TreeMap<>();

    public void add(Row row, String tableName, List<String> variables) {
        Map<String, String> variableToColumn = variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> String.format("%s.%s", tableName, variable)));
        allColumns.addAll(variableToColumn.values());

        String samptime = row.getLocalDateTime("samptime").toString();
        if (!timeSeries.containsKey(samptime)) {
            timeSeries.put(samptime, new TreeMap<>());
        }
        variableToColumn.forEach((variable, column) ->
                timeSeries.get(samptime).put(column, row.getDouble(variable)));
    }

    public Map<String, Map<String, Double>> build() {
        fillNullValues();
        return timeSeries;
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
