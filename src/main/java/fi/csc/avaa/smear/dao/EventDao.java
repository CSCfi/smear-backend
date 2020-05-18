package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Event;
import fi.csc.avaa.smear.table.EventRecord;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static fi.csc.avaa.smear.table.EventTable.EVENT;
import static fi.csc.avaa.smear.table.VariableEventTable.VARIABLE_EVENT;

@ApplicationScoped
public class EventDao {

    @Inject
    DSLContext create;

    private final RecordMapper<EventRecord, Event> recordToEvent = record ->
            Event.builder()
                    .id(record.get(EVENT.ID))
                    .eventType(record.get(EVENT.EVENT_TYPE))
                    .description(record.get(EVENT.DESCRIPTION))
                    .responsiblePerson(record.get(EVENT.RESPONSIBLE_PERSON))
                    .periodStart(record.get(EVENT.PERIOD_START))
                    .periodEnd(record.get(EVENT.PERIOD_END))
                    .timestamp(record.get(EVENT.TIMESTAMP))
                    .build();

    public List<Event> findByVariableIds(List<Long> variableIds) {
        return create
                .select()
                .from(EVENT)
                .join(VARIABLE_EVENT)
                .on(EVENT.ID.eq(VARIABLE_EVENT.EVENT_ID))
                .where(VARIABLE_EVENT.VARIABLE_ID.in(variableIds))
                .fetchInto(EVENT)
                .map(recordToEvent);
    }

    public Event findById(Long id) {
        return create
                .selectFrom(EVENT)
                .where(EVENT.ID.eq(id))
                .fetchOne(recordToEvent);
    }
}
