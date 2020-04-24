package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class VariableMetadataDao extends SmearDao {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "table-metadata-cache")
    public Uni<VariableMetadata> findById(Long id) {
        return client
                .preparedQuery("SELECT * FROM VariableMetadata WHERE variableID = ?", Tuple.of(id))
                .map(rowSet -> toStream(rowSet)
                        .map(VariableMetadata::from)
                        .findFirst()
                        .orElseThrow()
                );
    }
}
