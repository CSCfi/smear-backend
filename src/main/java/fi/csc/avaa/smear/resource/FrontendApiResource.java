package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.DataStructureDao;
import fi.csc.avaa.smear.dto.datastructure.StationNode;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class FrontendApiResource {

    @Inject
    DataStructureDao dataStructureDao;

    @GET
    @Path(Endpoints.AGGREGATION_TYPES)
    @Operation(summary = "Fetch aggregation type search optons for UI")
    public List<Map<String, ? extends Serializable>> aggregationTypes() {
        return Aggregation.valuesAsMaps();
    }

    @GET
    @Path(Endpoints.QUALITY_TYPES)
    @Operation(summary = "Fetch quality type search options and their "
        + "default values for the UI")
    public List<Map<String, ? extends Serializable>> qualityTypes() {
        return Quality.valuesAsMaps();
    }

    @GET
    @Path(Endpoints.DATA_STRUCTURE)
    @Operation(summary = "Fetch data for navigation tree on the UI search page")
    public List<StationNode> dataStructure() {
        return dataStructureDao.fetchDataStructure();
    }
}
