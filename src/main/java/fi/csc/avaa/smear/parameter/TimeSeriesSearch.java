package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.constants.AggregationInterval;
import fi.csc.avaa.smear.constants.AggregationType;
import fi.csc.avaa.smear.validation.ValidIsoDate;
import fi.csc.avaa.smear.validation.ValidTimeSeriesSearch;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@ValidTimeSeriesSearch
public class TimeSeriesSearch {

    @QueryParam("table")
    public String table;

    @QueryParam("variable")
    public List<String> variables;

    @QueryParam("tablevariable")
    public List<@Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tablevariables;

    @QueryParam("from")
    @NotNull
    @ValidIsoDate
    public String from;

    @QueryParam("to")
    @NotNull
    @ValidIsoDate
    public String to;

    @QueryParam("quality")
    @NotNull
    @NotEmpty
    public String quality;

    @QueryParam("aggregation_type")
    public String aggregationTypeStr;

    @QueryParam("aggregation_interval")
    public String aggregationIntervalStr;

    @QueryParam("cuv_no")
    @NotNull
    @NotEmpty
    public String cuv_no;

    public List<String[]> getTableVariablePairs() {
        return tablevariables
                .stream()
                .map(s -> s.split("\\."))
                .collect(Collectors.toList());
    }

    public Timestamp getFromTimestamp() {
        return Timestamp.valueOf(from.replace('T', ' '));
    }

    public Timestamp getToTimestamp() {
        return Timestamp.valueOf(to.replace('T', ' '));
    }

    public AggregationType getAggregationType() {
        return AggregationType.from(aggregationTypeStr.toUpperCase());
    }

    public AggregationInterval getAggregationInterval() {
        return AggregationInterval.from(aggregationIntervalStr.toUpperCase());
    }
}
