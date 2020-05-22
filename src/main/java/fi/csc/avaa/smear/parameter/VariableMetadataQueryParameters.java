package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidVariableMetadataQueryParameters;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
@ValidVariableMetadataQueryParameters
public class VariableMetadataQueryParameters {

    @Parameter(description = "Name of the database table where variable data is stored in the smear database. " +
            "Table names can be found from the tablemetadata endpoint by variables tableIDs. " +
            "Table id for every variable can be found from variable's metadata record via the " +
            "variable metadata endpoint. Multiple parameters can be used.",
            example = "HYY_META")
    @QueryParam("table_id")
    private List<String> table_id;

    @Parameter(description = "Name of a variable in the SMEAR database. Multiple parameters can be used. " +
            "At least one is required if there is at least one tableId parameter.",
            example = "Pamb0")
    @QueryParam("variable")
    private List<String> variable;

    @Parameter(description = "Name of a table and a variable separated by a period. " +
            "Multiple parameters can be used.",
            example = "HYY_META.Pamb0")
    @QueryParam("tablevariable")
    private List<@Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tablevariable;

    @Parameter(description = "Name of a category in the SMEAR database. Multiple parameters can be used.",
            example = "aerosol")
    @QueryParam("category")
    private List<String> category;

    @Parameter(description = "Source of the variable. The parameter will be used to do a text search. " +
            "Multiple parameters can be used.")
    @QueryParam("source")
    private List<String> source;

    @Parameter(description = "Unique id of a variable. Multiple parameters can be used",
            example = "1")
    @QueryParam("variable_id")
    private List<String> variable_id;
}
