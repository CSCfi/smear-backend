package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.TagDao;
import fi.csc.avaa.smear.dto.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.SEARCH_TAGS)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

    @Inject
    TagDao dao;

    @GET
    @Operation(
            summary = "Fetch tags by variable name",
            description = "Returns tags linked to a SMEAR variable. Tags are keywords for SMEAR variables. " +
                    "They are used to link variables/columns to vocabularies or other standards."
    )
    public List<Tag> tagsByVariableNames(
            @NotNull
            @NotEmpty
            @Parameter(description = "Name of a SMEAR variable. Multiple parameters can be used.",
                    example = "Pamb0")
            @QueryParam("variable") List<String> variableNames
    ) {
        return dao.findByVariableNames(variableNames);
    }
}
