package fi.csc.avaa.smear.parameter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Quality {

    ANY, CHECKED;

    private static final List<String> queryParams = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toList());

    public static List<String> getQueryParams() {
        return queryParams;
    }

    public static Quality from(String queryParam) {
        return valueOf(queryParam.toUpperCase());
    }

    public static List<Map<String, ? extends Serializable>> valuesAsMaps() {
        return Arrays.stream(values())
                .map(quality -> Map.of(
                        "id", quality.name()
                ))
                .collect(Collectors.toList());
    }
}
