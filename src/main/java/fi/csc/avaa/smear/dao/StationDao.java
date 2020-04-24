package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class StationDao extends SmearDao {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "station-cache")
    public Uni<List<Station>> findAll() {
        return client
                .query("SELECT stationid, name FROM station")
                .map(rowSet ->
                        toStream(rowSet)
                                .map(Station::from)
                                .collect(Collectors.toList())
                );
    }
}
