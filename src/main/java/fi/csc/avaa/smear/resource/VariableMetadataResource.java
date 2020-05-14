package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.dto.VariableMetadataTable;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.smallrye.mutiny.Uni;

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

    /*
        TODO:
        search should return all tables if no table/tablevariable provided?
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Uni<VariableMetadata> variableMetadata(@NotNull @PathParam("id") Long id) {
        return dao.findById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    public Uni<List<VariableMetadata>> search(@BeanParam @Valid VariableMetadataSearch search) {
        return dao.search(search);
    }

    @GET
    @Produces("text/csv")
    @Path("/search/csv")
    public String searchCsv(@BeanParam @Valid VariableMetadataSearch search) {
        List<VariableMetadata> result = dao.search(search).await().indefinitely();
        return VariableMetadataTable.csv(result);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/search/tsv")
    public String searchTsv(@BeanParam @Valid VariableMetadataSearch search) {
        List<VariableMetadata> result = dao.search(search).await().indefinitely();
        return VariableMetadataTable.tsv(result);
    }
}
