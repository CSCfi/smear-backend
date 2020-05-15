package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

public class TableMetadata {

    public Long id;
    public Long stationId;
    public String identifier;
    public String name;
    public String title;
    public String spatialCoverage;
    public Long period;
    public LocalDateTime timestamp;

    public static TableMetadata from(Record record) {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.id = record.get(field("tableID"), Long.class);
        tableMetadata.stationId = record.get(field("stationID"), Long.class);
        tableMetadata.identifier = record.get(field("identifier"), String.class);
        tableMetadata.name = record.get(field("name"), String.class);
        tableMetadata.title = record.get(field("title"), String.class);
        tableMetadata.spatialCoverage = record.get(field("spatial_coverage"), String.class);
        tableMetadata.period = record.get(field("period"), Long.class);
        tableMetadata.timestamp = record.get(field("ttimestamp"), LocalDateTime.class);
        return tableMetadata;
    }
}
