package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.dto.VariableMetadataTable;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.VARIABLE_METADATA)
public class VariableMetadataResource {

    @Inject
    VariableMetadataDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(
            summary = "Fetch variable metadata by variable id",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public VariableMetadata variableMetadata(
            @NotNull
            @Parameter(description = "Unique id of a variable",
                example = "1")
            @PathParam("id") Long id
    ) {
        return dao.findById(id);
    }

    /*
        TODO:
        return all tables if no table/tablevariable provided, separate findall endpoint or both
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    @Operation(
            summary = "Search variable metadata",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public List<VariableMetadata> search(@BeanParam @Valid VariableMetadataSearch search) {
        return dao.search(search);
    }

    @GET
    @Produces("text/csv")
    @Path("/search/csv")
    @Operation(
            summary = "Search variable metadata, CSV format",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public String searchCsv(@BeanParam @Valid VariableMetadataSearch search) {
        return VariableMetadataTable.csv(dao.search(search));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/search/tsv")
    @Operation(
            summary = "Search variable metadata, TSV format",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public String searchTsv(@BeanParam @Valid VariableMetadataSearch search) {
        return VariableMetadataTable.tsv(dao.search(search));
    }
}
