package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.Tag;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class TagDao {

    @Inject
    MySQLPool client;

    private final DSLContext create = DSL.using(SQLDialect.MYSQL);

    public Uni<List<Tag>> findByVariableIds(List<String> variableIds) {
        Query query = create
                .select()
                .from("Tags")
                .join("variableTags")
                .on(field("Tags.tagID").eq(field("variableTags.TagID")))
                .where(field("variableTags.variableID").in(variableIds));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(Tag::from)
                        .collect(Collectors.toList())
                );
    }
}
