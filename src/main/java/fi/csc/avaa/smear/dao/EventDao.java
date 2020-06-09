package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Event;
import fi.csc.avaa.smear.table.EventRecord;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.dao.QueryUtils.toTableAndVariableConditions;
import static fi.csc.avaa.smear.table.EventTable.EVENT;
import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableEventTable.VARIABLE_EVENT;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;

@ApplicationScoped
public class EventDao {

    @Inject
    DSLContext create;

    private final RecordMapper<EventRecord, Event> recordToEvent = record ->
            Event.builder()
                    .id(record.get(EVENT.ID))
                    .eventType(record.get(EVENT.EVENT_TYPE))
                    .description(record.get(EVENT.DESCRIPTION))
                    .periodStart(record.get(EVENT.PERIOD_START))
                    .periodEnd(record.get(EVENT.PERIOD_END))
                    .timestamp(record.get(EVENT.TIMESTAMP))
                    .build();

    public List<Event> findByTablesAndVariables(Map<String, List<String>> tableToVariables) {
        return create
                .select()
                .from(EVENT)
                .join(VARIABLE_EVENT)
                .on(EVENT.ID.eq(VARIABLE_EVENT.EVENT_ID))
                .join(VARIABLE_METADATA)
                .on(VARIABLE_EVENT.VARIABLE_ID.eq(VARIABLE_METADATA.ID))
                .join(TABLE_METADATA)
                .on(VARIABLE_METADATA.TABLE_ID.eq(TABLE_METADATA.ID))
                .where(toTableAndVariableConditions(tableToVariables))
                .fetchInto(EVENT)
                .map(recordToEvent);
    }
}
