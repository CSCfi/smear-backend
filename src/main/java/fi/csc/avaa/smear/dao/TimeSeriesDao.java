package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TimeSeries;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import io.vertx.mutiny.mysqlclient.MySQLPool;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.timestampDiff;
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

    @CacheResult(cacheName = "time-series-search-cache")
    public Map<String, Map<String, Double>> search(TimeSeriesSearch search) {
        Field<Timestamp> samptime = field("samptime", TIMESTAMP);
        TimeSeries timeSeries = new TimeSeries();
        search.getTablesAndVariables().forEach((tableName, variables) -> {
            Table<Record> table = table(tableName);
            List<String> columns = variables
                    .stream()
                    .map(variable -> String.format("%s.%s", tableName, variable))
                    .collect(Collectors.toList());

            List<SelectFieldOrAsterisk> fields = new ArrayList<>();
            fields.add(samptime);
            fields.addAll(columns
                    .stream()
                    .map(column -> avg(field(column, FLOAT)).as(column))
                    .collect(Collectors.toList()));

            Field<Integer> timestampDiff = timestampDiff(MINUTE, field("'1990-1-1'", TIMESTAMP), samptime);
            int interval = search.getAggregationInterval().getMinutes();

            Query query = create
                    .select(fields)
                    .from(table)
                    .where(samptime.between(search.getFromTimestamp(), search.getToTimestamp()))
                    .groupBy(floor(timestampDiff.div(interval)));

            client
                    .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                    .map(DaoUtils::toStream)
                    .await().indefinitely()
                    .forEach(row -> timeSeries.add(row, columns));
        });
        return timeSeries.get();
    }
}
