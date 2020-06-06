package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.DataStructureDao;
import fi.csc.avaa.smear.dao.StationDao;
import fi.csc.avaa.smear.dao.TableMetadataDao;
import fi.csc.avaa.smear.dao.VariableMetadataDao;
import fi.csc.avaa.smear.dto.Station;
import fi.csc.avaa.smear.dto.TableMetadata;
import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.dto.datastructure.StationNode;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SmearResource {

    @Inject
    StationDao stationDao;

    @Inject
    TableMetadataDao tableMetadataDao;

    @Inject
    VariableMetadataDao variableMetadataDao;

    @Inject
    DataStructureDao dataStructureDao;

    @GET
    @Path(Endpoints.DATA_STRUCTURE)
    public List<StationNode> dataStructure() {
        return dataStructureDao.fetchDataStructure();
    }

    @GET
    @Path(Endpoints.STATIONS)
    public List<Station> stations() {
        return stationDao.findAll();
    }

    @GET
    @Path(Endpoints.STATION)
    public Station station(@PathParam("stationId") Long stationId) {
        return stationDao.findById(stationId);
    }

    @GET
    @Path(Endpoints.TABLES)
    public List<TableMetadata> tables(@PathParam("stationId") Long stationId) {
        return tableMetadataDao.findByStationId(stationId);
    }

    @GET
    @Path(Endpoints.TABLE)
    public TableMetadata table(@PathParam("tableId") Long tableId) {
        return tableMetadataDao.findById(tableId);
    }

    @GET
    @Path(Endpoints.VARIABLES)
    public List<VariableMetadata> variables(@PathParam("tableId") Long tableId) {
        return variableMetadataDao.findByTableId(tableId);
    }

    @GET
    @Path(Endpoints.VARIABLE)
    public VariableMetadata variable(@PathParam("variableId") Long variableId) {
        return variableMetadataDao.findById(variableId);
    }
}
