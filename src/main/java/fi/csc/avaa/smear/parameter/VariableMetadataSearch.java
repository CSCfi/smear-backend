package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidVariableMetadataSearch;
import lombok.EqualsAndHashCode;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ValidVariableMetadataSearch
public class VariableMetadataSearch {

    @Parameter(description = "Name of the database table where variable data is stored in the smear database. " +
            "Table names can be found from the tablemetadata endpoint by variables tableIDs. " +
            "Table id for every variable can be found from variable's metadata record via the " +
            "variable metadata endpoint. Multiple parameters can be used.",
            example = "HYY_META")
    @QueryParam("tableId")
    public List<String> tableIds;

    @Parameter(description = "Name of a variable in the SMEAR database. Multiple parameters can be used. " +
            "At least one is required if there is at least one tableId parameter.",
            example = "Pamb0")
    @QueryParam("variable")
    public List<String> variables;

    @Parameter(description = "Name of a table and a variable separated by a period. " +
            "Multiple parameters can be used.",
            example = "HYY_META.Pamb0")
    @QueryParam("tablevariable")
    public List<@Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tablevariables;

    @Parameter(description = "Name of a category in the SMEAR database. Multiple parameters can be used.",
            example = "aerosol")
    @QueryParam("category")
    public List<String> categories;

    @Parameter(description = "Source of the variable. The parameter will be used to do a text search. " +
            "Multiple parameters can be used.")
    @QueryParam("source")
    public List<String> sources;

    @Parameter(description = "Unique id of a variable. Multiple parameters can be used",
            example = "1")
    @QueryParam("variableId")
    public List<String> variableIds;

    public Map<String, String> getTableToVariable() {
        return tablevariables
                .stream()
                .map(s -> s.split("\\."))
                .collect(Collectors.toMap(
                        split -> split[0],
                        split -> split[1]));
    }
}
