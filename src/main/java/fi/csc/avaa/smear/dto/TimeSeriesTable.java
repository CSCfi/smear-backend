package fi.csc.avaa.smear.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public final class TimeSeriesTable {

    public static String csv(Map<String, Map<String, Object>> timeSeries) {
        return toTable(timeSeries, ",");
    }

    public static String tsv(Map<String, Map<String, Object>> timeSeries) {
        return toTable(timeSeries, "\t");
    }

    private static String toTable(Map<String, Map<String, Object>> timeSeries, String delimiter) {
        StringBuilder builder = new StringBuilder();
        if (!timeSeries.isEmpty()) {
            boolean headerDone = false;
            for (Entry<String, Map<String, Object>> entry : timeSeries.entrySet()) {
                String samptime = entry.getKey();
                Map<String, Object> columnToValues = entry.getValue();
                Set<String> columns = columnToValues.keySet();
                if (!headerDone) {
                    builder.append("Year,Month,Day,Hour,Minute,Second,");
                    builder.append(String.join(delimiter, columns));
                    builder.append("\n");
                    headerDone = true;
                }
                builder.append(String.join(delimiter, splitDatetime(samptime)));
                builder.append(delimiter);
                String values = columns
                        .stream()
                        .map(columnToValues::get)
                        .map(Object::toString)
                        .collect(Collectors.joining(delimiter));
                builder.append(values);
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private static List<String> splitDatetime(String isoDateTime) {
        String year = isoDateTime.substring(0, 4);
        String month = isoDateTime.substring(5, 7);
        String day = isoDateTime.substring(8, 10);
        String hour = isoDateTime.substring(11, 13);
        String minute = isoDateTime.substring(14, 16);
        String second = isoDateTime.substring(17, 19);
        return Arrays.asList(year, month, day, hour, minute, second);
    }
}
