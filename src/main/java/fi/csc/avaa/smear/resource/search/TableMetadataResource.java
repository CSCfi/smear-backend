package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.dto.TableMetadata;
import fi.csc.avaa.smear.config.Endpoints;
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

@Path(Endpoints.SEARCH_TABLES)
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
    public List<TableMetadata> allTableMetadata() {
        return dao.findAll();
    }

    @GET
    @Path("/{name}")
    @Operation(
            summary = "Fetch table metadata by table name",
            description = "Table metadata describes measuring stations (=database tables)."
    )
    public TableMetadata tableMetadata(
            @NotNull
            @Parameter(description = "Name of a table in the SMEAR database.",
                    example = "HYY_META")
            @PathParam("name") String name
    ) {
        return dao.findByName(name);
    }
}
