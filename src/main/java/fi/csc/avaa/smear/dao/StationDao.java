package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Station;
import fi.csc.avaa.smear.table.StationRecord;
import io.quarkus.cache.CacheResult;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static fi.csc.avaa.smear.table.StationTable.STATION;

@ApplicationScoped
public class StationDao {

    @Inject
    DSLContext create;

    private final RecordMapper<StationRecord, Station> recordToStation = record ->
            Station.builder()
                    .id(record.get(STATION.ID))
                    .name(record.get(STATION.NAME))
                    .build();

    @CacheResult(cacheName = "station-cache")
    public List<Station> findAll() {
        return create
                .selectFrom(STATION)
                .fetch(recordToStation);
    }
}
