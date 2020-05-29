package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.dao.TimeSeriesDao;
import fi.csc.avaa.smear.dto.TimeSeries;
import fi.csc.avaa.smear.dto.TimeSeriesFormatter;
import fi.csc.avaa.smear.parameter.TimeSeriesQueryParameters;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import fi.csc.avaa.smear.config.Endpoints;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public TimeSeries timeSeries(@BeanParam @Valid TimeSeriesQueryParameters params) {
        TimeSeriesSearch search = TimeSeriesSearch.from(params);
        return dao.search(search);
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
        TimeSeriesSearch search = TimeSeriesSearch.from(params);
        return TimeSeriesFormatter.toCsv(dao.search(search));
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
        TimeSeriesSearch search = TimeSeriesSearch.from(params);
        return TimeSeriesFormatter.toTsv(dao.search(search));
    }
}
