package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import fi.csc.avaa.smear.table.TableMetadataRecord;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;

@ApplicationScoped
public class TableMetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<TableMetadataRecord, TableMetadata> recordToTableMetadata = record ->
            TableMetadata.builder()
                    .id(record.get(TABLE_METADATA.ID))
                    .stationId(record.get(TABLE_METADATA.STATION_ID))
                    .identifier(record.get(TABLE_METADATA.IDENTIFIER))
                    .name(record.get(TABLE_METADATA.NAME))
                    .title(record.get(TABLE_METADATA.TITLE))
                    .spatialCoverage(record.get(TABLE_METADATA.SPATIAL_COVERAGE))
                    .period(record.get(TABLE_METADATA.PERIOD))
                    .timestamp(record.get(TABLE_METADATA.TIMESTAMP))
                    .build();

    @CacheResult(cacheName = "table-metadata-list-cache")
    public List<TableMetadata> findAll() {
        return create
                .selectFrom(TABLE_METADATA)
                .fetch(recordToTableMetadata);
    }

    @CacheResult(cacheName = "table-metadata-cache")
    public TableMetadata findByName(String name) {
        return create
                .selectFrom(TABLE_METADATA)
                .where(TABLE_METADATA.NAME.eq(name))
                .fetchOne(recordToTableMetadata);
    }

    @CacheResult(cacheName = "table-name-cache")
    public List<String> findTableNames() {
        return create
                .select(TABLE_METADATA.NAME)
                .from(TABLE_METADATA)
                .fetch(record -> record.get(TABLE_METADATA.NAME));
    }
}
