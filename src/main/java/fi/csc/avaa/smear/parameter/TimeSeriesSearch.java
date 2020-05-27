package fi.csc.avaa.smear.parameter;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@EqualsAndHashCode
public class TimeSeriesSearch {

    private Map<String, List<String>> tableToVariables;
    private LocalDateTime from;
    private LocalDateTime to;
    private Quality quality;
    private Aggregation aggregation;
    private int interval;
    private List<Integer> cuvNos;

    public static TimeSeriesSearch from(TimeSeriesQueryParameters params) {
        Aggregation aggregation = params.getAggregation() != null
                ? Aggregation.from(params.getAggregation().toUpperCase())
                : Aggregation.NONE;
        int interval = params.getInterval() != null
                ? params.getInterval()
                : 30;
        Quality quality = params.getQuality() != null
                ? Quality.from(params.getQuality().toUpperCase())
                : Quality.ANY;
        return TimeSeriesSearch.builder()
                .tableToVariables(tableToVariablesMapFrom(params))
                .from(LocalDateTime.parse(params.getFrom()))
                .to(LocalDateTime.parse(params.getTo()))
                .quality(quality)
                .aggregation(aggregation)
                .interval(interval)
                .cuvNos(params.getCuv_no())
                .build();
    }

    private static Map<String, List<String>> tableToVariablesMapFrom(TimeSeriesQueryParameters params) {
        Map<String, List<String>> tableToVariables = new HashMap<>();
        if (params.getTable() != null && !params.getTable().isEmpty()) {
            tableToVariables.put(params.getTable(), params.getVariable());
        } else {
            params.getTablevariable().forEach(pair -> {
                String[] split = pair.split("\\.");
                String table = split[0];
                String variable = split[1];
                if (!tableToVariables.containsKey(table)) {
                    tableToVariables.put(table, new ArrayList<>());
                }
                tableToVariables.get(table).add(variable);
            });
        }
        return tableToVariables;
    }
}
