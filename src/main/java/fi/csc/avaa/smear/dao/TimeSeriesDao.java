package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.constants.AggregationInterval;
import fi.csc.avaa.smear.constants.AggregationType;
import fi.csc.avaa.smear.dto.TimeSeriesBuilder;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
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
import static org.jooq.impl.DSL.exp;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.ln;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.FLOAT;
import static org.jooq.impl.SQLDataType.TIMESTAMP;

/*
 TODO:
  QUALITY
  HYY_TREE special case
  HYYSLOWQueries
  AvailabilityQueries
 */
@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    private static final Field<Timestamp> SAMPTIME = field("samptime", TIMESTAMP);

    @CacheResult(cacheName = "time-series-search-cache")
    public Map<String, Map<String, Double>> search(TimeSeriesSearch search) {
        TimeSeriesBuilder builder = new TimeSeriesBuilder();
        search.getTablesAndColumns().forEach((tableName, columns) -> {
            Query query = createQuery(tableName, columns,
                    search.getFromTimestamp(), search.getToTimestamp(),
                    search.getAggregationType(), search.getAggregationInterval());

            client
                    .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                    .map(DaoUtils::toStream)
                    .await().indefinitely()
                    .forEach(row -> builder.add(row, columns));
        });
        return builder.build();
    }

    private Query createQuery(String tableName,
                              List<String> columns,
                              Timestamp from,
                              Timestamp to,
                              AggregationType aggregationType,
                              AggregationInterval aggregationInterval) {
        Table<Record> table = table(tableName);

        List<SelectFieldOrAsterisk> fields = new ArrayList<>();
        fields.add(SAMPTIME);
        fields.addAll(columns
                .stream()
                .map(column -> toField(column, aggregationType).as(column))
                .collect(Collectors.toList()));

        SelectConditionStep<Record> baseQuery = create
                .select(fields)
                .from(table)
                .where(SAMPTIME.between(from, to));

        if (aggregationType.equals(AggregationType.NONE)
                || aggregationType.equals(AggregationType.MEDIAN)) {
            return baseQuery;
        } else {
            Field<Integer> timestampDiff = timestampDiff(MINUTE, field("'1990-1-1'", TIMESTAMP), SAMPTIME);
            int interval = aggregationInterval.getMinutes();
            return baseQuery.groupBy(floor(timestampDiff.div(interval)));
        }
    }

    private Field<? extends Number> toField(String column, AggregationType aggregationType) {
        Field<Double> field = field(column, FLOAT);
        switch (aggregationType) {
            case ARITHMETIC:
                return avg(field);
            case GEOMETRIC:
                return exp(avg(ln(field)));
            case SUM:
                return sum(field);
            case MIN:
                return min(field);
            case MAX:
                return max(field);
            default:
                return field;
        }
    }
}
