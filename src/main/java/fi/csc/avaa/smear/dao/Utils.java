package fi.csc.avaa.smear.dao;

import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Utils {

    protected static Stream<Row> toStream(RowSet<Row> rowSet) {
        return StreamSupport.stream(rowSet.spliterator(), false);
    }

    protected static String toListArg(List<String> list) {
        return String.join(", ", list);
    }
}
