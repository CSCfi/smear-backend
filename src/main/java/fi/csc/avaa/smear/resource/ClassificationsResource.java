package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.VariableClassificationDao;
import fi.csc.avaa.smear.dto.Classifications;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ClassificationsResource {

    @Inject
    VariableClassificationDao variableClassificationDao;

    @GET
    @Path(Endpoints.CLASSIFICATIONS)
    @Operation(
            summary = "Fetch classifications",
            description = "Available aggregation/quality types and variable classification by station and category. " +
                    "Used for configuring the SMEAR frontend."
    )
    public Classifications classifications() {
        return Classifications.builder()
                .variableClassification(variableClassificationDao.fetchVariableClassification())
                .build();
    }
}
