package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TableMetadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

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

    private final String table = "TableMetadata";
    private final DSLContext create = DSL.using(SQLDialect.MYSQL);

    @CacheResult(cacheName = "table-metadata-list-cache")
    public Uni<List<TableMetadata>> findAll() {
        Query query = create
                .select()
                .from(table);
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
                .from(table)
                .where(field("tableID").eq(id));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(RowSet::iterator)
                .map(iter -> iter.hasNext()
                        ? TableMetadata.from(iter.next())
                        : null
                );
    }
}
