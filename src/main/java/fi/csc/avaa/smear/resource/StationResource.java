package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.StationDao;
import fi.csc.avaa.smear.dto.Station;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Path(Endpoints.STATIONS)
public class StationResource {

    @Inject
    StationDao dao;

    @GET
    @Operation(summary = "Fetch list of all SMEAR stations")
    public List<Station> stations() {
        return dao.findAll();
    }
}
