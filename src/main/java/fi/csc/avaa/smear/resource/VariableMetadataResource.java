package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.VariableMetadata;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(Endpoints.VARIABLE_METADATA)
@Produces(MediaType.APPLICATION_JSON)
public class VariableMetadataResource {

    @Inject
    VariableMetadataDao dao;

    @GET
    @Path("/{id}")
    public Uni<VariableMetadata> variableMetadata(@NotNull @PathParam("id") Long id) {
        return dao.findById(id);
    }
}
