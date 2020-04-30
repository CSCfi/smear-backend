package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidVariableMetadataSearch;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.stream.Collectors;

@ValidVariableMetadataSearch
public class VariableMetadataSearch {

    @QueryParam("variable")
    public List<String> variables;

    @QueryParam("tablevariable")
    public List<@Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tablevariables;

    @QueryParam("category")
    public List<String> categories;

    @QueryParam("source")
    public List<String> sources;

    @QueryParam("variable_id")
    public List<String> variableIds;

    @QueryParam("table_id")
    public List<String> tableIds;

    public List<String[]> getTableVariablePairs() {
        return tablevariables
                .stream()
                .map(s -> s.split("\\."))
                .collect(Collectors.toList());
    }
}
