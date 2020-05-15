package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Event;
import org.jooq.DSLContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class EventDao {

    @Inject
    DSLContext create;

    public List<Event> findByVariableIds(List<Integer> variableIds) {
        return create
                .select()
                .from("Events")
                .join("variableEvent")
                .on(field("Events.eventID").eq(field("variableEvent.eventID")))
                .where(field("variableEvent.variableID").in(variableIds))
                .fetch(Event::from);
    }

    public Event findById(Integer id) {
        return create
                .select()
                .from("Events")
                .where(field("eventID").eq(id))
                .fetchOne(Event::from);
    }
}
