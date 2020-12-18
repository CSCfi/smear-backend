package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.StationDao;
import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.Station;
import fi.csc.avaa.smear.dto.TableMetadata;
import fi.csc.avaa.smear.dto.VariableMetadata;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RestResource {

    @Inject
    StationDao stationDao;

    @Inject
    TableMetadataDao tableMetadataDao;

    @Inject
    VariableMetadataDao variableMetadataDao;

    @GET
    @Path(Endpoints.STATIONS)
    @Operation(summary = "Fetch general info about the Smart SMEAR stations")
    public List<Station> stations() {
        return stationDao.findAll();
    }

    @GET
    @Path(Endpoints.STATION)
    @Operation(summary = "Fetch general info about a Smart SMEAR station")
    public Station station(
            @Parameter(
                description = "Id of a station in the SMEAR database.",
                example = "1")
            @PathParam("stationId") Long stationId
    ) {
        return stationDao.findById(stationId);
    }

    @GET
    @Path(Endpoints.TABLES)
    @Operation(summary = "Fetch general info about tables for a Smart SMEAR "
        + "station")
    public List<TableMetadata> tables(
            @Parameter(
                description = "Id of a station in the SMEAR database.",
                example = "1")
            @PathParam("stationId") Long stationId
    ) {
        return tableMetadataDao.findByStationId(stationId);
    }

    @GET
    @Path(Endpoints.TABLE)
    @Operation(summary = "Fetch general info about a table for a Smart SMEAR "
        + "station")
    public TableMetadata table(
            @Parameter(
                description = "Id of a station in the SMEAR database.",
                example = "1"
            )
            @PathParam("stationId")
            Long stationId,
            @Parameter(
                description = "Id of a table in the SMEAR database.",
                example = "11"
            )
            @PathParam("tableId")
            Long tableId
    ) {
        return tableMetadataDao.findById(tableId);
    }

    @GET
    @Path(Endpoints.VARIABLES)
    @Operation(summary = "Fetch general info about variables in a table for a "
        + "Smart SMEAR station")
    public List<VariableMetadata> variables(
            @Parameter(
                description = "Id of a station in the SMEAR database.",
                example = "1"
            )
            @PathParam("stationId")
            Long stationId,
            @Parameter(
                description = "Id of a table in the SMEAR database.",
                example = "11"
            )
            @PathParam("tableId")
            Long tableId
    ) {
        return variableMetadataDao.findByTableId(tableId);
    }

    @GET
    @Path(Endpoints.VARIABLE)
    @Operation(summary = "Fetch general info about a variable in a table for "
        + "a Smart SMEAR station")
    public VariableMetadata variable(
            @Parameter(
                description = "Id of a station in the SMEAR database.",
                example = "1"
            )
            @PathParam("stationId")
            Long stationId,
            @Parameter(
                description = "Id of a table in the SMEAR database.",
                example = "11"
            )
            @PathParam("tableId")
            Long tableId,
            @Parameter(
                description = "Id of a variable in the SMEAR database.",
                example = "528"
            )
            @PathParam("variableId")
            Long variableId
    ) {
        return variableMetadataDao.findById(variableId);
    }
}
