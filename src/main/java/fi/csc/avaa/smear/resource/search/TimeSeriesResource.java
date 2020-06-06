package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.TimeSeriesDao;
import fi.csc.avaa.smear.dto.timeseries.TimeSeriesSheet;
import fi.csc.avaa.smear.dto.timeseries.TimeSeriesSheetFormatter;
import fi.csc.avaa.smear.parameter.TimeSeriesQueryParameters;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path(Endpoints.SEARCH_TIMESERIES)
public class TimeSeriesResource {

    @Inject
    TimeSeriesDao dao;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database",
            description = "Information about stored variables can be found via the Metadata API " +
                    "or the graphical SMART SMEAR application."
    )
    public TimeSeriesSheet timeSeries(@BeanParam @Valid TimeSeriesQueryParameters params) {
        return dao.getSheet(TimeSeriesSearch.from(params));
    }

    @GET
    @Path("/chart")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database as chart series",
            description = "Information about stored variables can be found via the Metadata API " +
                    "or the graphical SMART SMEAR application. " +
                    "This endpoint is mainly for frontend use."
    )
    public Map<String, List<List<Number>>> timeSeriesChart(@BeanParam @Valid TimeSeriesQueryParameters params) {
        return dao.getChart(TimeSeriesSearch.from(params));
    }

    @GET
    @Path("/csv")
    @Produces("text/csv")
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database, CSV format",
            description = "Information about stored variables can be found via the Metadata API " +
                    "or the graphical SMART SMEAR application."
    )
    public String timeSeriesCsv(@BeanParam @Valid TimeSeriesQueryParameters params) {
        TimeSeriesSheet table = dao.getSheet(TimeSeriesSearch.from(params));
        return TimeSeriesSheetFormatter.toCsv(table);
    }

    @GET
    @Path("/tsv")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database, TSV format",
            description = "Information about stored variables can be found via Metadata API " +
                    "or via the graphical SMART SMEAR application."
    )
    public String timeSeriesTxt(@BeanParam @Valid TimeSeriesQueryParameters params) {
        TimeSeriesSheet table = dao.getSheet(TimeSeriesSearch.from(params));
        return TimeSeriesSheetFormatter.toTsv(table);
    }
}
