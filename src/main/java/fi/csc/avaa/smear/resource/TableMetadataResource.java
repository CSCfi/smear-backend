package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.dto.TableMetadata;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.TABLE_METADATA)
@Produces(MediaType.APPLICATION_JSON)
public class TableMetadataResource {

    @Inject
    TableMetadataDao dao;

    @GET
    @Path("/")
    public Uni<List<TableMetadata>> allTableMetadata() {
        return dao.findAll();
    }

    @GET
    @Path("/{id}")
    public Uni<TableMetadata> tableMetadata(@NotNull @PathParam("id") Long id) {
        return dao.findById(id);
    }
}
