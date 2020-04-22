package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.dao.SmearDao;
import fi.csc.avaa.smear.dto.Station;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class SmearResource {

    @Inject
    SmearDao smearDao;

    @GET
    @Path("/stations")
    public Uni<List<Station>> hello() {
        return smearDao.getStations();
    }
}
