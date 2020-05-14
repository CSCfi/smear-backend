package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.jooq.impl.DSL.field;

public class Event {

    public Long id;
    public String eventType;
    public String description;
    public String responsiblePerson;
    public LocalDate periodStart;
    public LocalDate periodEnd;
    public LocalDateTime timestamp;

    public static Event from(Record record) {
        Event event = new Event();
        event.id = record.get(field("eventID"), Long.class);
        event.eventType = record.get(field("event_type"), String.class);
        event.description = record.get(field("event"), String.class);
        event.responsiblePerson = record.get(field("who"), String.class);
        event.periodStart = record.get(field("period_start"), LocalDate.class);
        event.periodEnd = record.get(field("period_end"), LocalDate.class);
        event.timestamp = record.get(field("etimestamp"), LocalDateTime.class);
        return event;
    }
}
