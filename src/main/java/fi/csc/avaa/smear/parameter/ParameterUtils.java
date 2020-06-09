package fi.csc.avaa.smear.parameter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ParameterUtils {

    public static Map<String, List<String>> mapTablesToVariables(List<String> tableVariables) {
        return tableVariables
                .stream()
                .map(s -> s.split("\\."))
                .collect(Collectors.groupingBy(
                        split -> split[0],
                        Collectors.mapping(split -> split[1], Collectors.toList())));
    }
}
