package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.parameter.TimeSeriesParameters;
import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.TimeSeriesDao;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path(Endpoints.TIMESERIES)
public class TimeSeriesResource {

    @Inject
    TimeSeriesDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Map<String, String>> timeSeries(@BeanParam @Valid TimeSeriesParameters params) {
        return dao.find(params);
    }

    @GET
    @Path("/csv")
    @Produces("text/csv")
    public Response timeSeriesCsv(@BeanParam @Valid TimeSeriesParameters params) {
        return Response.ok("hello,foo,bar")
                .header("Content-Disposition", "attachment; filename=smeardata.csv")
                .build();
    }

    @GET
    @Path("/tsv")
    @Produces(MediaType.TEXT_PLAIN)
    public Response timeSeriesTxt(@BeanParam @Valid TimeSeriesParameters params) {
        return Response.ok("hello\tfoo\tbar")
                .header("Content-Disposition", "attachment; filename=smeardata.txt")
                .build();
    }
}
