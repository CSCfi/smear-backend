package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.dto.VariableMetadataFormatter;
import fi.csc.avaa.smear.parameter.VariableMetadataQueryParameters;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.SEARCH_VARIABLES)
public class VariableMetadataResource {

    @Inject
    VariableMetadataDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Fetch all variable metadata",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public List<VariableMetadata> allVariableMetadata() {
        return dao.findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    @Operation(
            summary = "Search variable metadata",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public List<VariableMetadata> variableMetadata(@BeanParam @Valid VariableMetadataQueryParameters params) {
        VariableMetadataSearch search = VariableMetadataSearch.from(params);
        return dao.search(search);
    }

    @GET
    @Produces("text/csv")
    @Path("/search/csv")
    @Operation(
            summary = "Search variable metadata, CSV format",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public String variableMetadataCsv(@BeanParam @Valid VariableMetadataQueryParameters params) {
        VariableMetadataSearch search = VariableMetadataSearch.from(params);
        return VariableMetadataFormatter.toCsv(dao.search(search));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/search/tsv")
    @Operation(
            summary = "Search variable metadata, TSV format",
            description = "Metadata that describes variables stored in the SMEAR database."
    )
    public String variableMetadataTsv(@BeanParam @Valid VariableMetadataQueryParameters params) {
        VariableMetadataSearch search = VariableMetadataSearch.from(params);
        return VariableMetadataFormatter.toTsv(dao.search(search));
    }
}
