package fi.csc.avaa.smear.parameter;

import java.util.Arrays;
import java.util.List;
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
}
