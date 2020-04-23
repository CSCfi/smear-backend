package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.dao.SmearDao;
import fi.csc.avaa.smear.dto.Station;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api")
public class SmearResource {

    @Inject
    SmearDao smearDao;

    @GET
    @Path("/stations")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Station>> stations() {
        return smearDao.getStations();
    }

    @GET
    @Path("/smeardata")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> smearData(
            @QueryParam("variables") String variables,
            @QueryParam("table") String table
    ) {
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        return map;
    }

    @GET
    @Path("/smeardata/csv")
    @Produces("text/csv")
    public Response smearDataCsv(
            @QueryParam("variables") String variables,
            @QueryParam("table") String table
    ) {
        return Response.ok("hello,foo,bar")
                .header("Content-Disposition", "attachment; filename=smeardata.csv")
                .build();
    }

    @GET
    @Path("/smeardata/tsv")
    @Produces(MediaType.TEXT_PLAIN)
    public Response smearDataTxt(
            @QueryParam("variables") String variables,
            @QueryParam("table") String table
    ) {
        return Response.ok("hello\tfoo\tbar")
                .header("Content-Disposition", "attachment; filename=smeardata.txt")
                .build();
    }
}
