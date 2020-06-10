package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.TagDao;
import fi.csc.avaa.smear.dto.Tag;
import fi.csc.avaa.smear.validation.Patterns;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.parameter.ParameterUtils.mapTablesToVariables;

@Path(Endpoints.SEARCH_TAGS)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

    @Inject
    TagDao dao;

    @GET
    @Operation(
            summary = "Fetch tags by table and variable.",
            description = "Returns tags linked to a SMEAR variable. Tags are keywords for SMEAR variables. " +
                    "They are used to link variables/columns to vocabularies or other standards."
    )
    public List<Tag> search(
            @Parameter(description = "Name of a table and a variable separated by a period. " +
                    "Table and variable names can be queried from the table and variable metadata endpoints. " +
                    "Multiple parameters can be used and at least one is required.",
                    example = "HYY_META.Pamb0")
            @NotNull
            @NotEmpty
            @QueryParam("tablevariable") List<@NotEmpty @Pattern(regexp = Patterns.TABLEVARIABLE) String> tablevariable
    ) {
        Map<String, List<String>> tableToVariables = mapTablesToVariables(tablevariable);
        return dao.findByTablesAndVariables(tableToVariables);
    }
}
