package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

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

    public static VariableMetadata from(Record record) {
        VariableMetadata variableMetadata = new VariableMetadata();
        variableMetadata.id = record.get(field("variableID"), Long.class);
        variableMetadata.tableId = record.get(field("tableID"), Long.class);
        variableMetadata.name = record.get(field("variable"), String.class);
        variableMetadata.description = record.get(field("description"), String.class);
        variableMetadata.type = record.get(field("type"), String.class);
        variableMetadata.unit = record.get(field("unit"), String.class);
        variableMetadata.title = record.get(field("title"), String.class);
        variableMetadata.source = record.get(field("source"), String.class);
        variableMetadata.periodStart = record.get(field("period_start"), LocalDateTime.class);
        variableMetadata.periodEnd = record.get(field("period_end"), LocalDateTime.class);
        variableMetadata.coverage = record.get(field("coverage"), Integer.class);
        variableMetadata.rights = record.get(field("rights"), String.class);
        variableMetadata.category = record.get(field("category"), String.class);
        variableMetadata.mandatory = record.get(field("mandatory"), Boolean.class);
        variableMetadata.derivative = record.get(field("derivative"), Boolean.class);
        variableMetadata.uiSortOrder = record.get(field("ui_sort_order"), Integer.class);
        variableMetadata.uiAvgType = record.get(field("ui_avg_type"), String.class);
        variableMetadata.timestamp = record.get(field("vtimestamp"), LocalDateTime.class);
        return variableMetadata;
    }
}
