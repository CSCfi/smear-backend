package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Event;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class EventDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, Event> recordToEvent = record ->
            Event.builder()
                    .id(record.get(field("eventID"), Long.class))
                    .eventType(record.get(field("event_type"), String.class))
                    .description(record.get(field("event"), String.class))
                    .responsiblePerson(record.get(field("who"), String.class))
                    .periodStart(record.get(field("period_start"), LocalDate.class))
                    .periodEnd(record.get(field("period_end"), LocalDate.class))
                    .timestamp(record.get(field("etimestamp"), LocalDateTime.class))
                    .build();

    public List<Event> findByVariableIds(List<Integer> variableIds) {
        return create
                .select()
                .from("Events")
                .join("variableEvent")
                .on(field("Events.eventID").eq(field("variableEvent.eventID")))
                .where(field("variableEvent.variableID").in(variableIds))
                .fetch(recordToEvent);
    }

    public Event findById(Integer id) {
        return create
                .select()
                .from("Events")
                .where(field("eventID").eq(id))
                .fetchOne(recordToEvent);
    }
}
