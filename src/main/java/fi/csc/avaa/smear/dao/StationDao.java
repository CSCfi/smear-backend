package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class StationDao {

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "station-cache")
    public List<Station> findAll() {
        return create
                .select(field("stationid"), field("name"))
                .from("station")
                .fetch(Station::from);
    }
}
