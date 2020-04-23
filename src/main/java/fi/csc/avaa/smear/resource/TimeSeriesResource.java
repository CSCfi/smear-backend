package fi.csc.avaa.smear.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path(Endpoints.TIMESERIES)
public class TimeSeriesResource {

    @Inject
    TimeSeriesDao timeSeriesDao;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Map<String, String>> timeSeries(
            @NotEmpty @QueryParam("variables") List<String> variables,
            @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @QueryParam("from")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @NotNull @QueryParam("to")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
            @NotEmpty @QueryParam("quality") String quality,
            @NotEmpty @QueryParam("averaging") String averaging,
            @NotEmpty @QueryParam("type") String type,
            @NotEmpty @QueryParam("cuv_no") String cuv_no
    ) {
        return timeSeriesDao.getTimeSeries(
                variables,
                table,
                tablevariables,
                from,
                to,
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
            @NotEmpty @QueryParam("variables") List<String> variables,
            @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @QueryParam("from")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @NotNull @QueryParam("to")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
            @NotEmpty @QueryParam("quality") String quality,
            @NotEmpty @QueryParam("averaging") String averaging,
            @NotEmpty @QueryParam("type") String type,
            @NotEmpty @QueryParam("cuv_no") String cuv_no
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
            @NotEmpty @QueryParam("variables") List<String> variables,
            @NotEmpty @QueryParam("table") String table,
            @QueryParam("tablevariables") List<String> tablevariables,
            @NotNull @QueryParam("from")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @NotNull @QueryParam("to")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to,
            @NotEmpty @QueryParam("quality") String quality,
            @NotEmpty @QueryParam("averaging") String averaging,
            @NotEmpty @QueryParam("type") String type,
            @NotEmpty @QueryParam("cuv_no") String cuv_no
    ) {
        // TODO
        return Response.ok("hello\tfoo\tbar")
                .header("Content-Disposition", "attachment; filename=smeardata.txt")
                .build();
    }
}
