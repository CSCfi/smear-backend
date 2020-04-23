package fi.csc.avaa.smear.dao;

import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SmearDao {

    protected static Stream<Row> toStream(RowSet<Row> rowSet) {
        return StreamSupport.stream(rowSet.spliterator(), false);
    }
}
