package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.TagDao;
import fi.csc.avaa.smear.dto.Tag;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.TAGS)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {

    @Inject
    TagDao dao;

    @GET
    @Operation(
            summary = "Fetch tags by variable id",
            description = "Returns tags linked to a SMEAR variable by the variable's id. Tags are keywords for SMEAR " +
                    "variables. They are used to link variables/columns to vocabularies or other standards."
    )
    public List<Tag> tagsByVariableIds(
            @NotNull
            @NotEmpty
            @Parameter(description = "Unique id of a SMEAR variable. Multiple parameters can be used.",
                    example = "1")
            @QueryParam("variableId") List<String> variableIds
    ) {
        return dao.findByVariableIds(variableIds);
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Fetch tag by id",
            description = "Returns a single tag by it's unique id. Tags are keywords for SMEAR variables. They are " +
                    "used to link variables/columns to vocabularies or other standards."
    )
    public List<Tag> tag(
            @NotNull
            @Parameter(description = "Unique id of a tag",
                    example = "1")
            @PathParam("id") Integer id
    ) {
        return dao.findById(id);
    }
}
