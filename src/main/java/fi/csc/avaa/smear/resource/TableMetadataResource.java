package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.dto.TableMetadata;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

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
    @Operation(
            summary = "Fetch all table metadata",
            description = "Table metadata describes measuring stations (=database tables)."
    )
    public Uni<List<TableMetadata>> allTableMetadata() {
        return dao.findAll();
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Fetch table metadata by table id",
            description = "Table metadata describes measuring stations (=database tables)."
    )
    public Uni<TableMetadata> tableMetadata(
            @NotNull
            @Parameter(description = "A unique table id. The table id of a SMEAR variable can be found via " +
                    "the variable metadata endpoint.",
                    example = "16")
            @PathParam("id") Long id
    ) {
        return dao.findById(id);
    }
}
