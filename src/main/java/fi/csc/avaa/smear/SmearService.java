package fi.csc.avaa.smear;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class SmearService {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "foo-cache")
    public Uni<String> getFoo() {
        return client
                .query("SELECT * FROM test")
                .map(rowSet ->
                        toStream(rowSet)
                                .map(row -> row.getString("foo"))
                                .findFirst()
                                .orElseThrow()
                );
    }

    private static Stream<Row> toStream(RowSet<Row> rowSet) {
        return StreamSupport.stream(rowSet.spliterator(), false);
    }
}
