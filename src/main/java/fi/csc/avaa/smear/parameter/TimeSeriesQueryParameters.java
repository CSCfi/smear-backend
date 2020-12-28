package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.Patterns;
import fi.csc.avaa.smear.validation.ValidIsoDate;
import fi.csc.avaa.smear.validation.ValidTimeSeriesQueryParameters;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
@ValidTimeSeriesQueryParameters
public class TimeSeriesQueryParameters {

    @Parameter(description = "Name of a table and a variable separated by a period. " +
            "Table and variable names can be queried from the table metadata and variable metadata endpoints. " +
            "Multiple parameters can be used and at least one is required. Variables in results may not be in " +
            "the same order as in the query.",
            required = true)
    @QueryParam("tablevariable")
    @NotEmpty
    private List<@NotEmpty @Pattern(regexp = Patterns.TABLEVARIABLE) String> tablevariable;

    @Parameter(description = "Time series start time (inclusive) in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm).",
            example = "2016-02-11T00:00:00.989",
            required = true)
    @NotNull
    @ValidIsoDate
    @QueryParam("from")
    private String from;

    @Parameter(description = "Time series end time (exclusive) in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm).",
            example = "2016-02-12T09:06:07.989",
            required = true)
    @QueryParam("to")
    @NotNull
    @ValidIsoDate
    private String to;

    @Parameter(description = "Should the time series data be quality checked or not. " +
            "Valid values: ANY (default), CHECKED.",
            example = "ANY")
    @QueryParam("quality")
    private String quality;

    @Parameter(description = "Type of the sample time aggregation. " +
            "Valid values: NONE (default), ARITHMETIC, GEOMETRIC, SUM, MEDIAN, MIN, MAX, CIRCULAR, AVAILABILITY. " +
            "MEDIAN and CIRCULAR are not supported when querying HYY_SLOW or HYY_TREE table. AVAILABILITY returns " +
            "integer value of hunderth of a percent of the available measurements with given criteria without " +
            "considering specified interval value.",
            example = "NONE")
    @QueryParam("aggregation")
    private String aggregation;

    @Parameter(description = "Sample time aggregation interval in minutes. Valid range is from 1 to 60.",
            example = "30")
    @Min(1)
    @Max(60)
    @QueryParam("interval")
    private Integer interval;

    @Parameter(description = "cuv_no values in the SMEAR database. Multiple parameters can be used. " +
            "If not specified when querying HYY_TREE table all of the rows in the given time interval " +
            "are fetched.")
    @QueryParam("cuv_no")
    private List<Integer> cuv_no;
}
