package fi.csc.avaa.smear.parameter;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@EqualsAndHashCode
public class VariableMetadataSearch {

    private List<String> tables;
    private List<String> variables;
    private Map<String, String> tableToVariable;
    private List<String> categories;
    private List<String> sources;

    public static VariableMetadataSearch from(VariableMetadataQueryParameters params) {
        Map<String, String> tableToVariable = params.getTablevariable()
                .stream()
                .map(s -> s.split("\\."))
                .collect(Collectors.toMap(
                        split -> split[0],
                        split -> split[1]));
        return VariableMetadataSearch.builder()
                .tables(params.getTable())
                .variables(params.getVariable())
                .tableToVariable(tableToVariable)
                .categories(params.getCategory())
                .sources(params.getSource())
                .build();
    }
}
