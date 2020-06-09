package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.Patterns;
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
            "Table names can be found from the tablemetadata endpoint. Multiple parameters can be used.",
            example = "HYY_META")
    @QueryParam("table")
    private List<String> table;

    @Parameter(description = "Name of a variable in the SMEAR database. Multiple parameters can be used.",
            example = "Pamb0")
    @QueryParam("variable")
    private List<String> variable;

    @Parameter(description = "Name of a table and a variable separated by a period. " +
            "Multiple parameters can be used.",
            example = "HYY_META.Pamb0")
    @QueryParam("tablevariable")
    private List<@Pattern(regexp = Patterns.TABLEVARIABLE) String> tablevariable;

    @Parameter(description = "Name of a category in the SMEAR database. " +
            "The parameter will be used to do a text search. " +
            "Multiple parameters can be used.",
            example = "aerosol")
    @QueryParam("category")
    private List<String> category;

    @Parameter(description = "Description of the variable. " +
            "The parameter will be used to do a text search. " +
            "Multiple parameters can be used.")
    @QueryParam("description")
    private List<String> description;

    @Parameter(description = "Source of the variable. " +
            "The parameter will be used to do a text search. " +
            "Multiple parameters can be used.")
    @QueryParam("source")
    private List<String> source;
}
