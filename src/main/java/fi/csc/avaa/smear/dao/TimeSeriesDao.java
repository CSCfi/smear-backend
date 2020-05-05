package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;

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
import static org.jooq.DatePart.MINUTE;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.FLOAT;
import static org.jooq.impl.SQLDataType.TIMESTAMP;

/*
 TODO:
  HYY_TREE special case
  QUALITY
  HYYSLOWQueries
  AvailabilityQueries
 */
@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    private static final String COL_SAMPTIME = "samptime";

    @CacheResult(cacheName = "time-series-search-cache")
    public Uni<List<Map<String, Object>>> search(TimeSeriesSearch search) {
        Table<Record> table = table(search.table);
        Field<Timestamp> samptime = field(COL_SAMPTIME, TIMESTAMP);
        Map<String, String> columnNames = search.variables
                .stream()
                .collect(Collectors.toMap(
                        variable -> variable,
                        variable -> String.format("%s.%s", table, variable)));

        List<SelectFieldOrAsterisk> variables = new ArrayList<>();
        variables.add(samptime);
        variables.addAll(search.variables
                .stream()
                .map(variable ->
                        avg(field(variable, FLOAT))
                                .as(columnNames.get(variable)))
                .collect(Collectors.toList()));

        Field<Integer> timestampDiff = timestampDiff(MINUTE, field("'1990-1-1'", TIMESTAMP), samptime);
        int interval = search.getAggregationInterval().getMinutes();

        Query query = create
                .select(variables)
                .from(table)
                .where(samptime.between(search.getFromTimestamp(), search.getToTimestamp()))
                .groupBy(floor(timestampDiff.div(interval)));

        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(row -> toMap(row, columnNames))
                        .collect(Collectors.toList()));
    }

    private static Map<String, Object> toMap(Row row, Map<String, String> colnames) {
        Map<String, Object> map = new HashMap<>();
        // TODO: check dates
        map.put(COL_SAMPTIME, row.getLocalDateTime(COL_SAMPTIME));
        colnames.forEach((variable, colname) ->
                map.put(variable, row.getDouble(colname)));
        return map;
    }
}
