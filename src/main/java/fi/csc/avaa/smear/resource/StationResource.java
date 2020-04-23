package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.SmearDao;
import fi.csc.avaa.smear.dto.Station;
import io.smallrye.mutiny.Uni;

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
    SmearDao smearDao;

    @GET
    public Uni<List<Station>> stations() {
        return smearDao.getStations();
    }
}
