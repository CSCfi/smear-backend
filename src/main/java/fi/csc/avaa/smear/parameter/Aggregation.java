package fi.csc.avaa.smear.parameter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Aggregation {

    NONE(Grouping.NONE),
    ARITHMETIC(Grouping.IN_QUERY),
    GEOMETRIC(Grouping.IN_QUERY),
    SUM(Grouping.IN_QUERY),
    MEDIAN(Grouping.MANUAL),
    MIN(Grouping.IN_QUERY),
    MAX(Grouping.IN_QUERY),
    AVAILABILITY(Grouping.NONE),
    CIRCULAR(Grouping.MANUAL);

    public enum Grouping {
        NONE, IN_QUERY, MANUAL;
    }

    private final Grouping type;

    Aggregation(Grouping type) {
        this.type = type;
    }

    public boolean isGroupedManually() {
        return type.equals(Grouping.MANUAL);
    }

    public boolean isGroupedInQuery() {
        return type.equals(Grouping.IN_QUERY);
    }

    private static final List<String> queryParams = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toList());

    public static List<String> getQueryParams() {
        return queryParams;
    }

    public static Aggregation from(String queryParam) {
        return valueOf(queryParam.toUpperCase());
    }

    public static List<Map<String, ? extends Serializable>> valuesAsMaps() {
        return Arrays.stream(values())
                .map(aggregation -> Map.of(
                        "id", aggregation.name(),
                        "isGroupedManually", aggregation.isGroupedManually()
                ))
                .collect(Collectors.toList());
    }
}
