package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class StationDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record2<Object, Object>, Station> recordToStation = record ->
            Station.builder()
                    .id(record.get(field("stationid"), Integer.class))
                    .name(record.get(field("name"), String.class))
                    .build();

    @CacheResult(cacheName = "station-cache")
    public List<Station> findAll() {
        return create
                .select(field("stationid"), field("name"))
                .from("station")
                .fetch(recordToStation);
    }
}
