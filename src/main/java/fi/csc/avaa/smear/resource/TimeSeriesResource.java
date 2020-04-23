package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.TimeSeriesDao;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Path(Endpoints.TIMESERIES)
public class TimeSeriesResource {

    @Inject
    TimeSeriesDao timeSeriesDao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Map<String, String>> timeSeries(
            @NotNull @NotEmpty @QueryParam("variables") List<String> variables,
            @NotNull @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @NotEmpty @QueryParam("from") String from,
            @NotNull @NotEmpty @QueryParam("to") String to,
            @NotNull @NotEmpty @QueryParam("quality") String quality,
            @NotNull @NotEmpty @QueryParam("averaging") String averaging,
            @NotNull @NotEmpty @QueryParam("type") String type,
            @NotNull @NotEmpty @QueryParam("cuv_no") String cuv_no
    ) {
        System.out.println(table);
        return timeSeriesDao.getTimeSeries(
                variables,
                table,
                tablevariables,
                LocalDateTime.now(),
                LocalDateTime.now(),
                quality,
                averaging,
                type,
                cuv_no
        );
    }

    @GET
    @Path("/csv")
    @Produces("text/csv")
    public Response timeSeriesCsv(
            @NotNull @NotEmpty @QueryParam("variables") List<String> variables,
            @NotNull @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @NotEmpty @QueryParam("from") String from,
            @NotNull @NotEmpty @QueryParam("to") String to,
            @NotNull @NotEmpty @QueryParam("quality") String quality,
            @NotNull @NotEmpty @QueryParam("averaging") String averaging,
            @NotNull @NotEmpty @QueryParam("type") String type,
            @NotNull @NotEmpty @QueryParam("cuv_no") String cuv_no
    ) {
        // TODO
        return Response.ok("hello,foo,bar")
                .header("Content-Disposition", "attachment; filename=smeardata.csv")
                .build();
    }

    @GET
    @Path("/tsv")
    @Produces(MediaType.TEXT_PLAIN)
    public Response timeSeriesTxt(
            @NotNull @NotEmpty @QueryParam("variables") List<String> variables,
            @NotNull @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @NotEmpty @QueryParam("from") String from,
            @NotNull @NotEmpty @QueryParam("to") String to,
            @NotNull @NotEmpty @QueryParam("quality") String quality,
            @NotNull @NotEmpty @QueryParam("averaging") String averaging,
            @NotNull @NotEmpty @QueryParam("type") String type,
            @NotNull @NotEmpty @QueryParam("cuv_no") String cuv_no
    ) {
        // TODO
        return Response.ok("hello\tfoo\tbar")
                .header("Content-Disposition", "attachment; filename=smeardata.txt")
                .build();
    }
}
