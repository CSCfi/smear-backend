package fi.csc.avaa.smear.dto.timeseries;

import fi.csc.avaa.smear.parameter.Aggregation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dto.DateTimeFormat.ISO8601_DATETIME_FORMATTER;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COLNAME_SAMPTIME;

public class TimeSeriesSheetBuilder extends TimeSeriesBuilder<TimeSeriesSheet> {

    private final Set<String> allColumns = new HashSet<>();
    private final Map<String, Map<String, Object>> samptimeToValues = new TreeMap<>();

    public TimeSeriesSheetBuilder(LocalDateTime startTime, LocalDateTime endTime,
                                  Aggregation aggregation, Integer aggregationInterval) {
        super(startTime, endTime, aggregation, aggregationInterval);
    }

    @Override
    protected void addDataPoint(LocalDateTime samptime, String column, Number value) {
        if (samptime == null) {
            return;
        }
        allColumns.add(column);
        String samptimeStr = ISO8601_DATETIME_FORMATTER.format(samptime);
        initSamptime(samptimeStr);
        samptimeToValues.get(samptimeStr).put(column, value);
    }

    @Override
    public TimeSeriesSheet build() {
        List<Map<String, Object>> data = samptimeToValues.entrySet()
                .stream()
                .map(toRow)
                .collect(Collectors.toList());
        return TimeSeriesSheet.builder()
                .startTime(startTime)
                .endTime(endTime)
                .columns(allColumns)
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

    private void initSamptime(String samptime) {
        if (!samptimeToValues.containsKey(samptime)) {
            samptimeToValues.put(samptime, new TreeMap<>());
        }
    }
}
