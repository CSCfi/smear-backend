package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.time.LocalDateTime;

public class TableMetadata {

    public Long id;
    public Long stationId;
    public String identifier;
    public String name;
    public String title;
    public String spatialCoverage;
    public Long period;
    public LocalDateTime timestamp;

    public static TableMetadata from(Row row) {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.id = row.getLong("tableID");
        tableMetadata.stationId = row.getLong("stationID");
        tableMetadata.identifier = row.getString("identifier");
        tableMetadata.name = row.getString("name");
        tableMetadata.title = row.getString("title");
        tableMetadata.spatialCoverage = row.getString("spatial_coverage");
        tableMetadata.period = row.getLong("period");
        tableMetadata.timestamp = row.getLocalDateTime("timestamp");
        return tableMetadata;
    }
}
