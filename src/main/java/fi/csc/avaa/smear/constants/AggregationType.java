package fi.csc.avaa.smear.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AggregationType {

    NONE(false),
    ARITHMETIC(true),
    GEOMETRIC(true),
    SUM(true),
    MEDIAN(false),
    MIN(true),
    MAX(true),
    AVAILABILITY(false),
    CIRCULAR(false);

    private final boolean groupedByInterval;

    AggregationType(boolean groupedByInterval) {
        this.groupedByInterval = groupedByInterval;
    }

    public boolean isGroupedByInterval() {
        return groupedByInterval;
    }

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
