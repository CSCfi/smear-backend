package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidIsoDate;
import fi.csc.avaa.smear.validation.ValidTimeSeriesSearch;
import lombok.EqualsAndHashCode;
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
import java.util.stream.Collectors;

@ValidTimeSeriesSearch
@EqualsAndHashCode
public class TimeSeriesSearch {

    @Parameter(description = "Name of the database table where variable data is stored in the smear database. " +
            "If you want to select from multiple tables, please use multiple tablevariable parameters. " +
            "Table names can be queried from the tablemetadata endpoint by a variable's table id. " +
            "Table ids of variables can be found from the variablemetadata endpoint.",
            example = "HYY_META")
    @QueryParam("table")
    public String table;

    @Parameter(description = "Name of a variable in the SMEAR database. Multiple parameters can be used. " +
            "At least one is required if the table parameter is not empty.",
            example = "Pamb0")
    @QueryParam("variable")
    public List<@NotEmpty String> variables;

    @Parameter(description = "Name of a table and a variable separated by a period. " +
            "Multiple parameters can be used.",
            example = "HYY_META.Pamb0")
    @QueryParam("tablevariable")
    public List<@NotEmpty @Pattern(regexp = "[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+") String> tableVariables;

    @Parameter(description = "Time series start time in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm).",
            example = "2016-02-11T00:00:00.989",
            required = true)
    @QueryParam("from")
    @NotNull
    @ValidIsoDate
    public String from;

    @Parameter(description = "Time series end time in ISO 8601 format (YYYY-MM-DDThh:mm:ss.mmm).",
            example = "2016-02-12T09:06:07.989",
            required = true)
    @QueryParam("to")
    @NotNull
    @ValidIsoDate
    public String to;

    @Parameter(description = "Should the time series data be quality checked or not. " +
            "Valid values: ANY (default), CHECKED.",
            example = "ANY",
            required = true)
    @QueryParam("quality")
    public String qualityStr;

    @Parameter(description = "Type of the sample time aggregation. " +
            "Valid values: NONE (default), ARITHMETIC, GEOMETRIC, SUM, MEDIAN, MIN, MAX, CIRCULAR. " +
            "MEDIAN and CIRCULAR are not supported when querying HYY_SLOW or HYY_TREE table.",
            example = "NONE")
    @QueryParam("aggregation")
    public String aggregationStr;

    @Parameter(description = "Sample time aggregation interval. Valid values: 30MIN (default), 60MIN.",
            example = "30MIN")
    @QueryParam("aggregation_interval")
    public String aggregationIntervalStr;

    @Parameter(description = "cuv_no values in the SMEAR database. Multiple parameters can be used. " +
            "At least one value is required when querying HYY_TREE table.",
            example = "186")
    @QueryParam("cuv_no")
    public List<String> cuvNoStr;

    private Map<String, List<String>> tableToVariables;

    public Map<String, List<String>> getTableToVariables() {
        if (tableToVariables == null) {
            tableToVariables = new HashMap<>();
            if (table != null && !table.isEmpty()) {
                tableToVariables.put(table, variables);
            } else {
                tableVariables.forEach(pair -> {
                    String[] split = pair.split("\\.");
                    String tableName = split[0];
                    String variableName = split[1];
                    if (!tableToVariables.containsKey(tableName)) {
                        tableToVariables.put(tableName, new ArrayList<>());
                    }
                    tableToVariables.get(tableName).add(variableName);
                });
            }
        }
        return tableToVariables;
    }

    public Timestamp getFromTimestamp() {
        return Timestamp.valueOf(from.replace('T', ' '));
    }

    public Timestamp getToTimestamp() {
        return Timestamp.valueOf(to.replace('T', ' '));
    }

    public Quality getQuality() {
        return qualityStr != null
                ? Quality.from(qualityStr.toUpperCase())
                : Quality.ANY;
    }

    public Aggregation getAggregation() {
        return aggregationStr != null
                ? Aggregation.from(aggregationStr.toUpperCase())
                : Aggregation.NONE;
    }

    public AggregationInterval getAggregationInterval() {
        return aggregationIntervalStr != null
                ? AggregationInterval.from(aggregationIntervalStr.toUpperCase())
                : AggregationInterval.INTERVAL_30MIN;
    }

    public List<Integer> getCuvNos() {
        return cuvNoStr
                .stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
