package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class TableMetadataDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "table-metadata-list-cache")
    public Uni<List<TableMetadata>> findAll() {
        Query query = create
                .select()
                .from("TableMetadata");
        return client
                .preparedQuery(query.getSQL())
                .map(rowSet -> toStream(rowSet)
                        .map(TableMetadata::from)
                        .collect(Collectors.toList())
                );
    }

    @CacheResult(cacheName = "table-metadata-cache")
    public Uni<TableMetadata> findById(Long id) {
        Query query = create
                .select()
                .from("TableMetadata")
                .where(field("tableID").eq(id));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(TableMetadata::from)
                        .findFirst()
                        .orElseThrow()
                );
    }
}
