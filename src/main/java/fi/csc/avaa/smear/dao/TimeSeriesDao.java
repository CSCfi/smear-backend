package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.timestampDiff;
import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.table;

@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    public Uni<List<Map<String, Object>>> search(TimeSeriesSearch search) {
        Table<Record> table = table(search.table);
        // TODO
        Field<Object> samptime = field("samptime");
        List<SelectFieldOrAsterisk> variables = new ArrayList<>();
        variables.add(samptime);
        variables.addAll(search.variables
                .stream()
                .map(variable ->
                        avg(field(variable, SQLDataType.FLOAT)).as(variable))
                .collect(Collectors.toList()));
        Query query = create
                .select(variables)
                .from(table)
                .where(samptime.between(search.getFromDateTime(), search.getToDateTime()))
                .groupBy(
                        floor(timestampDiff(
                                DatePart.MINUTE,
                                field("'1990-1-1'", SQLDataType.TIMESTAMP),
                                field("samptime", SQLDataType.TIMESTAMP)
                        ).div(30)));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(row -> toMap(row, search.variables))
                        .collect(Collectors.toList()));
    }

    private static Map<String, Object> toMap(Row row, List<String> variables) {
        Map<String, Object> map = new HashMap<>();
        map.put("samptime", row.getLocalDateTime("samptime"));
        for (String variable : variables) {
            map.put(variable, row.getDouble(variable));
        }
        return map;
    }
}
