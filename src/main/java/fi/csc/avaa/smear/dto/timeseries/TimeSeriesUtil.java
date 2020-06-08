package fi.csc.avaa.smear.dto.timeseries;

import fi.csc.avaa.smear.parameter.Aggregation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.parameter.Aggregation.CIRCULAR;
import static fi.csc.avaa.smear.parameter.Aggregation.MEDIAN;

public final class TimeSeriesUtil {

    protected static String columnNameFor(String tableName, String variable) {
        return String.format("%s.%s", tableName, variable);
    }

    protected static Map<String, String> mapVariablesToColumnNames(String tableName, List<String> variables) {
        return variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> columnNameFor(tableName, variable)));
    }

    protected static LocalDateTime roundToNearestMinute(LocalDateTime timestamp) {
        if (timestamp.getSecond() >= 30) {
            return timestamp.plusMinutes(1).withSecond(0).withNano(0);
        }
        return timestamp.withSecond(0).withNano(0);
    }

    protected static double aggregateOf(List<Double> values, Aggregation aggregation) {
        if (aggregation.equals(MEDIAN)) {
            return medianOf(values);
        } else if (aggregation.equals(CIRCULAR)) {
            return circularMeanOf(values);
        } else {
            throw new UnsupportedOperationException(
                    String.format("Aggregation not supported: %s", aggregation.name()));
        }
    }

    private static double medianOf(List<Double> values) {
        Collections.sort(values);
        int noOfValues = values.size();
        if (noOfValues % 2 == 0) {
            return (values.get(noOfValues / 2) + values.get(noOfValues / 2 - 1)) / 2;
        } else {
            return values.get(noOfValues / 2);
        }
    }

    private static double circularMeanOf(List<Double> values) {
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
