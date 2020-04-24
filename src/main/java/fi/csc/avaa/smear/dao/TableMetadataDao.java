package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TableMetadataDao extends SmearDao {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "all-table-metadata-cache")
    public Uni<List<TableMetadata>> findAll() {
        return client
                .preparedQuery("SELECT * FROM TableMetadata")
                .map(rowSet -> toStream(rowSet)
                        .map(TableMetadata::from)
                        .collect(Collectors.toList())
                );
    }

    @CacheResult(cacheName = "table-metadata-cache")
    public Uni<TableMetadata> findById(Long id) {
        return client
                .preparedQuery("SELECT * FROM TableMetadata WHERE tableID = ?", Tuple.of(id))
                .map(RowSet::iterator)
                .map(iter -> iter.hasNext()
                        ? TableMetadata.from(iter.next())
                        : null
                );
    }
}
