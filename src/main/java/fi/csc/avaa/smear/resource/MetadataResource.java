package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.MetadataDao;
import fi.csc.avaa.smear.dto.Metadata;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(Endpoints.METADATA)
@Produces(MediaType.APPLICATION_JSON)
public class MetadataResource {

    @Inject
    MetadataDao dao;

    @GET
    public Uni<Metadata> metadata() {
        return dao.getMetadata();
    }
}
