package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Metadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import org.jooq.DSLContext;
import org.jooq.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;

@ApplicationScoped
public class MetadataDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "metadata-cache")
    public Uni<Metadata> getMetadata() {
        Query query = create
                .select()
                .from("Metadata");
        return client
                .query(query.getSQL())
                .map(rowSet -> toStream(rowSet)
                        .map(Metadata::from)
                        .findFirst()
                        .orElseThrow()
                );
    }
}
