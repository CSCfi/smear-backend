package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class SmearDao {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "station-cache")
    public Uni<List<Station>> getStations() {
        return client
                .query("SELECT stationid, name FROM station")
                .map(rowSet ->
                        toStream(rowSet)
                                .map(Station::from)
                                .collect(Collectors.toList())
                );
    }

    private static Stream<Row> toStream(RowSet<Row> rowSet) {
        return StreamSupport.stream(rowSet.spliterator(), false);
    }
}
