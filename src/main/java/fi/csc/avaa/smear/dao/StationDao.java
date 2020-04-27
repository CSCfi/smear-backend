package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
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
public class StationDao {

    @Inject
    MySQLPool client;

    private final String table = "station";
    private final DSLContext create = DSL.using(SQLDialect.MYSQL);

    @CacheResult(cacheName = "station-cache")
    public Uni<List<Station>> findAll() {
        Query query = create
                .select(field("stationid"), field("name"))
                .from(table);
        return client
                .query(query.getSQL())
                .map(rowSet ->
                        toStream(rowSet)
                                .map(Station::from)
                                .collect(Collectors.toList())
                );
    }
}
