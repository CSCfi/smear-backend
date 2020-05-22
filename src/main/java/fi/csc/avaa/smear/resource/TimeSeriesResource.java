package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.dao.TimeSeriesDao;
import fi.csc.avaa.smear.dto.TimeSeries;
import fi.csc.avaa.smear.dto.TimeSeriesFormatter;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(Endpoints.TIMESERIES)
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
    public TimeSeries timeSeries(@BeanParam @Valid TimeSeriesSearch params) {
        return dao.search(params);
    }

    @GET
    @Path("/csv")
    @Produces("text/csv")
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database, CSV format",
            description = "Information about stored variables can be found via the Metadata API " +
                    "or the graphical SMART SMEAR application."
    )
    public String timeSeriesCsv(@BeanParam @Valid TimeSeriesSearch params) {
        return TimeSeriesFormatter.toCsv(dao.search(params));
    }

    @GET
    @Path("/tsv")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Fetch time series data stored in the SMEAR Database, TSV format",
            description = "Information about stored variables can be found via Metadata API " +
                    "or via the graphical SMART SMEAR application."
    )
    public String timeSeriesTxt(@BeanParam @Valid TimeSeriesSearch params) {
        return TimeSeriesFormatter.toTsv(dao.search(params));
    }
}
