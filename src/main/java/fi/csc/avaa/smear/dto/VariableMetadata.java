package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.time.LocalDateTime;

public class VariableMetadata {

    public Long id;
    public Long tableId;
    public String name;
    public String description;
    public String type;
    public String unit;
    public String title;
    public String source;
    public LocalDateTime periodStart;
    public LocalDateTime periodEnd;
    public Integer coverage;
    public String rights;
    public String category;
    public Boolean mandatory;
    public Boolean derivative;
    public Integer uiSortOrder;
    public String uiAvgType;
    public LocalDateTime timestamp;

    public static VariableMetadata from(Row row) {
        VariableMetadata variableMetadata = new VariableMetadata();
        variableMetadata.id = row.getLong("variableID");
        variableMetadata.tableId = row.getLong("tableID");
        variableMetadata.name = row.getString("variable");
        variableMetadata.description = row.getString("description");
        variableMetadata.type = row.getString("type");
        variableMetadata.unit = row.getString("unit");
        variableMetadata.title = row.getString("title");
        variableMetadata.source = row.getString("source");
        variableMetadata.periodStart = row.getLocalDateTime("period_start");
        variableMetadata.periodEnd = row.getLocalDateTime("period_end");
        variableMetadata.coverage = row.getInteger("coverage");
        variableMetadata.rights = row.getString("rights");
        variableMetadata.category = row.getString("category");
        variableMetadata.mandatory = row.getInteger("mandatory").equals(1);
        variableMetadata.derivative = row.getInteger("derivative").equals(1);
        variableMetadata.uiSortOrder = row.getInteger("ui_sort_order");
        variableMetadata.uiAvgType = row.getString("ui_avg_type");
        variableMetadata.timestamp = row.getLocalDateTime("vtimestamp");
        return variableMetadata;
    }
}
