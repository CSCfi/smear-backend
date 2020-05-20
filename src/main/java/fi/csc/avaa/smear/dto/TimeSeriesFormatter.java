package fi.csc.avaa.smear.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TimeSeriesFormatter {

    public static String toCsv(TimeSeries timeSeries) {
        return toPlainText(timeSeries, ",");
    }

    public static String toTsv(TimeSeries timeSeries) {
        return toPlainText(timeSeries, "\t");
    }

    private static String toPlainText(TimeSeries timeSeries, String delimiter) {
        StringBuilder builder = new StringBuilder();
        if (timeSeries.getRecordCount() > 0) {
            boolean headerDone = false;
            for (Map<String, Object> values : timeSeries.getData()) {
                String samptime = values.get("samptime").toString();
                if (!headerDone) {
                    String samptimeColumns = String.join(delimiter, "Year", "Month", "Day", "Hour", "Minute", "Second");
                    builder.append(samptimeColumns);
                    builder.append(delimiter);
                    builder.append(String.join(delimiter, timeSeries.getColumns()));
                    builder.append("\n");
                    headerDone = true;
                }
                builder.append(String.join(delimiter, splitDatetime(samptime)));
                builder.append(delimiter);
                String delimitedValues = timeSeries.getColumns()
                        .stream()
                        .map(values::get)
                        .map(value -> value != null
                                ? value.toString()
                                : null)
                        .collect(Collectors.joining(delimiter));
                builder.append(delimitedValues);
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
