package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.MetadataDao;
import fi.csc.avaa.smear.dto.Metadata;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(Endpoints.SEARCH_METADATA)
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource {

    @Inject
    MetadataDao dao;

    @GET
    @Operation(summary = "Fetch general info about the Smart SMEAR application")
    public Metadata metadata() {
        return dao.getMetadata();
    }
}
