package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Metadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.RowSet;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MetadataDao {

    @Inject
    MySQLPool client;

    private final String table = "Metadata";
    private final DSLContext create = DSL.using(SQLDialect.MYSQL);

    @CacheResult(cacheName = "metadata-cache")
    public Uni<Metadata> getMetadata() {
        Query query = create
                .select()
                .from(table);
        return client
                .query(query.getSQL())
                .map(RowSet::iterator)
                .map(iter -> iter.hasNext()
                        ? Metadata.from(iter.next())
                        : null
                );
    }
}
