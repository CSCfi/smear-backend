package fi.csc.avaa.smear.parameter;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.parameter.ParameterUtils.mapTablesToVariables;

@Getter
@Builder
@EqualsAndHashCode
public class VariableMetadataSearch {

    private List<String> tables;
    private List<String> variables;
    private Map<String, List<String>> tableToVariables;
    private List<String> stations;
    private List<String> categories;
    private List<String> descriptions;
    private List<String> sources;

    public static VariableMetadataSearch from(VariableMetadataQueryParameters params) {
        return VariableMetadataSearch.builder()
                .tables(params.getTable())
                .variables(params.getVariable())
                .tableToVariables(mapTablesToVariables(params.getTablevariable()))
                .stations(params.getStation())
                .categories(params.getCategory())
                .descriptions(params.getDescription())
                .sources(params.getSource())
                .build();
    }
}
