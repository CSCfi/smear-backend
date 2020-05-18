package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.SQLDataType.VARCHAR;

@ApplicationScoped
public class TableMetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, TableMetadata> recordToTableMetadata = record ->
            TableMetadata.builder()
                    .id(record.get(field("tableID"), Long.class))
                    .stationId(record.get(field("stationID"), Long.class))
                    .identifier(record.get(field("identifier"), String.class))
                    .name(record.get(field("name"), String.class))
                    .title(record.get(field("title"), String.class))
                    .spatialCoverage(record.get(field("spatial_coverage"), String.class))
                    .period(record.get(field("period"), Long.class))
                    .timestamp(record.get(field("ttimestamp"), LocalDateTime.class))
                    .build();

    @CacheResult(cacheName = "table-metadata-list-cache")
    public List<TableMetadata> findAll() {
        return create
                .select()
                .from("TableMetadata")
                .fetch(recordToTableMetadata);
    }

    @CacheResult(cacheName = "table-metadata-cache")
    public TableMetadata findById(Long id) {
        return create
                .select()
                .from("TableMetadata")
                .where(field("tableID").eq(id))
                .fetchOne(recordToTableMetadata);
    }

    @CacheResult(cacheName = "table-name-cache")
    public List<String> findTableNames() {
        return create
                .select(field("name"))
                .from("TableMetadata")
                .fetch(record ->
                        record.get(field("name", VARCHAR)));
    }
}
