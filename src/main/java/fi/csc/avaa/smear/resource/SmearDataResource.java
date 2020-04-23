package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.SmearDao;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class SmearDataResource {

    @Inject
    SmearDao smearDao;

    @GET
    @Path(Endpoints.SMEARDATA)
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
    @Path(Endpoints.SMEARDATA_CSV)
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
    @Path(Endpoints.SMEARDATA_TSV)
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
