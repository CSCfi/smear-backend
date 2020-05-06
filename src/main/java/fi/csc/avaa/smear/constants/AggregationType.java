package fi.csc.avaa.smear.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AggregationType {

    NONE, ARITHMETIC, GEOMETRIC, SUM, MEDIAN, MIN, MAX, CIRCULAR;

    private static final List<String> queryParams = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toList());

    public static List<String> getQueryParams() {
        return queryParams;
    }

    public static AggregationType from(String queryParam) {
        return valueOf(queryParam.toUpperCase());
    }
}
