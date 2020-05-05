package fi.csc.avaa.smear.dao;

import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class DaoUtils {

    protected static Stream<Row> toStream(RowSet<Row> rowSet) {
        return StreamSupport.stream(rowSet.spliterator(), false);
    }

    // https://github.com/jOOQ/jOOQ/issues/4303
    protected static Field<Integer> timestampDiff(DatePart part, Field<Timestamp> t1, Field<Timestamp> t2) {
        return DSL.field("timestampdiff({0}, {1}, {2})", Integer.class, DSL.keyword(part.toSQL()), t1, t2);
    }
}
