package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Event;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class EventDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    public Uni<List<Event>> findByVariableIds(List<String> variableIds) {
        Query query = create
                .select()
                .from("Events")
                .join("variableEvent")
                .on(field("Events.eventID").eq(field("variableEvent.eventID")))
                .where(field("variableEvent.variableID").in(variableIds));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(Event::from)
                        .collect(Collectors.toList()));
    }

    public Uni<Event> findById(Integer id) {
        Query query = create
                .select()
                .from("Events")
                .where(field("eventID").eq(id));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(Event::from)
                        .findFirst()
                        .orElseThrow());
    }
}
