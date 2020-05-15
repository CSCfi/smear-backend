package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.SQLDataType.VARCHAR;

@ApplicationScoped
public class TableMetadataDao {

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "table-metadata-list-cache")
    public List<TableMetadata> findAll() {
        return create
                .select()
                .from("TableMetadata")
                .fetch(TableMetadata::from);
    }

    @CacheResult(cacheName = "table-metadata-cache")
    public TableMetadata findById(Long id) {
        return create
                .select()
                .from("TableMetadata")
                .where(field("tableID").eq(id))
                .fetchOne(TableMetadata::from);
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
