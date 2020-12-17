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

    protected static Double aggregateOf(List<Double> values, Aggregation aggregation) {
        if (aggregation.equals(MEDIAN)) {
            return medianOf(values);
        } else if (aggregation.equals(CIRCULAR)) {
            return circularMeanOf(values);
        } else {
            throw new UnsupportedOperationException(
                    String.format("Aggregation not supported: %s", aggregation.name()));
        }
    }

    private static Double medianOf(List<Double> values) {
        List<Double> dValues = values.stream().filter(v -> v != null).collect(Collectors.toList());
        Collections.sort(dValues);
        int noOfValues = dValues.size();
        if (noOfValues == 0) {
          return null;
        }
        Double result;
        if (noOfValues % 2 == 0) {
            result = (dValues.get(noOfValues / 2) + dValues.get(noOfValues / 2 - 1)) / 2;
        } else {
            result = dValues.get(noOfValues / 2);
        }
        return result.isNaN() ? null : result;
    }

    private static Double circularMeanOf(List<Double> values) {
        List<Double> dValues = values.stream().filter(v -> v != null).collect(Collectors.toList());
        Double s = 0.0;
        Double c = 0.0;
        for (Double value : dValues) {
            c += Math.cos(Math.toRadians(value));
            s += Math.sin(Math.toRadians(value));
        }
        c = c / dValues.size();
        s = s / dValues.size();
        Double sc = s / c;
        Double mean = Math.toDegrees(Math.atan(sc));
        if (c < 0.0) {
            mean += 180;
        } else if (s < 0.0 && c > 0.0) {
            mean += 360;
        }
        return mean.isNaN() ? null : Math.round(mean * 100) / 100.0;
    }
}
