package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.constants.AggregationInterval;
import fi.csc.avaa.smear.constants.AggregationType;
import fi.csc.avaa.smear.validation.ValidIsoDate;
import fi.csc.avaa.smear.validation.ValidTimeSeriesSearch;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.QueryParam;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ValidTimeSeriesSearch
public class TimeSeriesSearch {

    @Parameter(description = "Name of the database table where variable data is stored in the smear database. " +
            "If you want to select from multiple tables, please use multiple tablevariable parameters. " +
            "Table names can be found from the tablemetadata endpoint by variables tableIDs. " +
            "TableID for every variable can be found from variable's metadata record via the variablemetadata endpoint. ")
    @QueryParam("table")
    public String table;


    @QueryParam("variable")
    public List<String> variables;

    @Parameter(description = "Name of table and variable separated by a period (e.g. HYY_META.Pamb0). " +
            "Multiple tablevariable parameters can be used.")
    @QueryParam("tablevariable")
    public List<@Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tableVariables;

    @Parameter(description = "Time series start time in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm)",
            required = true)
    @QueryParam("from")
    @NotNull
    @ValidIsoDate
    public String from;

    @Parameter(description = "Time series end time in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm)",
            required = true)
    @QueryParam("to")
    @NotNull
    @ValidIsoDate
    public String to;

    @Parameter(description = "Should the time series data be quality checked or not. Valid values: ANY, CHECKED.",
            required = true)
    @QueryParam("quality")
    @NotNull
    @NotEmpty
    public String quality;

    @Parameter(description = "Type of the sample time aggregation. " +
            "Valid values: NONE (default), ARITHMETIC, GEOMETRIC, SUM, MEDIAN, MIN, MAX, CIRCULAR.")
    @QueryParam("aggregation_type")
    public String aggregationTypeStr;

    @Parameter(description = "Sample time aggregation interval. Valid values: 30MIN (default), 60MIN.")
    @QueryParam("aggregation_interval")
    public String aggregationIntervalStr;

    @Parameter(description = "cuv_no values in the SMEAR database. Multiple parameters can be used. At least one " +
            "parameter is required when selecting from the HYY_SLOW table.")
    @QueryParam("cuv_no")
    public List<String> cuv_no;

    public Map<String, List<String>> getTablesAndVariables() {
        Map<String, List<String>> map = new HashMap<>();
        if (table != null && !table.isEmpty()) {
            map.put(table, variables);
        } else {
            tableVariables
                    .stream()
                    .map(s -> s.split("\\."))
                    .forEach(tableVariablePair -> {
                        if (!map.containsKey(tableVariablePair[0])) {
                            map.put(tableVariablePair[0], new ArrayList<>());
                        }
                        map.get(tableVariablePair[0]).add(tableVariablePair[1]);
                    });
        }
        return map;
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
