package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event {

    public Long id;
    public String eventType;
    public String description;
    public String responsiblePerson;
    public LocalDate periodStart;
    public LocalDate periodEnd;
    public LocalDateTime timestamp;

    public static Event from(Row row) {
        Event event = new Event();
        event.id = row.getLong("eventID");
        event.eventType = row.getString("event_type");
        event.description = row.getString("event");
        event.responsiblePerson = row.getString("who");
        event.periodStart = row.getLocalDate("period_start");
        event.periodEnd = row.getLocalDate("period_end");
        event.timestamp = row.getLocalDateTime("etimestamp");
        return event;
    }
}
