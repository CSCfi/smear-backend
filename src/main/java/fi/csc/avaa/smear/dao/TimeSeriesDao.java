package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.constants.Aggregation;
import fi.csc.avaa.smear.constants.AggregationInterval;
import fi.csc.avaa.smear.constants.Quality;
import fi.csc.avaa.smear.dto.TimeSeriesBuilder;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.LikeEscapeStep;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectHavingStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

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
import static org.jooq.impl.DSL.case_;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.exp;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.ln;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.FLOAT;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.SQLDataType.TIMESTAMP;
import static org.jooq.impl.SQLDataType.VARCHAR;

/*
 TODO:
  AVAILABILITY
  HYY_TREE special case
  update openapi documentation (HYY_* queries)
  compare responses with production version, fix timestamps
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
        TimeSeriesBuilder builder = new TimeSeriesBuilder(search.getAggregation(), search.getAggregationInterval());
        search.getTableToVariables().forEach((tableName, variables) -> {
            if (tableName.equals("HYY_SLOW")) {
                Query query = createHyySlowQuery(variables, search);
                RowSet<Row> rowSet = client.preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                        .await().indefinitely();
                builder.addHyySlowRowSet(rowSet);
            } else {
                Query query = createQuery(tableName, variables, search);
                RowSet<Row> rowSet = client.preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                        .await().indefinitely();
                builder.add(rowSet, tableName, variables);
            }
        });
        return builder.build();
    }

    private Query createQuery(String tableName, List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = table(tableName);
        List<SelectFieldOrAsterisk> fields = getFields(variables, search.getQuality(), search.getAggregation());
        Condition samptimeInRange = SAMPTIME.between(search.getFromTimestamp(), search.getToTimestamp());
        SelectConditionStep<Record> baseQuery = create
                .select(fields)
                .from(table)
                .where(samptimeInRange);
        SelectHavingStep<Record> query = search.getAggregation().isGroupedInQuery()
                ? baseQuery.groupBy(getAggregateFunction(search.getAggregationInterval()))
                : baseQuery;
        return query.orderBy(SAMPTIME.asc());
    }

    private Query createHyySlowQuery(List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = table("HYY_SLOW");
        Field<Timestamp> startTime = field("start_time", TIMESTAMP);
        Field<String> variableName = field("variable", VARCHAR);
        Field<Double> value = field("value1", FLOAT);
        Condition startTimeInRange = startTime.between(search.getFromTimestamp(), search.getToTimestamp());
        Condition conditions = variables
                .stream()
                .map(variableName::eq)
                .reduce(noCondition(), Condition::or);
        return create
                .select(startTime, variableName, value)
                .from(table)
                .where(startTimeInRange)
                .and(conditions);
    }

    private List<SelectFieldOrAsterisk> getFields(List<String> variables, Quality quality,
                                                  Aggregation aggregation) {
        List<SelectFieldOrAsterisk> fields = new ArrayList<>();
        fields.add(SAMPTIME);
        fields.addAll(variables
                .stream()
                .map(variable -> toField(variable, quality, aggregation)
                        .as(variable))
                .collect(Collectors.toList()));
        return fields;
    }

    private Field<? extends Number> toField(String variable, Quality quality, Aggregation aggregation) {
        Field<Double> field = quality.equals(Quality.CHECKED)
                ? toQualityCheckedField(variable)
                : field(variable, FLOAT);
        switch (aggregation) {
            case ARITHMETIC:
                return avg(field);
            case AVAILABILITY:
                return count(field).div(count());
            case GEOMETRIC:
                return exp(avg(ln(field)));
            case MIN:
                return min(field);
            case MAX:
                return max(field);
            case SUM:
                return sum(field);
            default:
                return field;
        }
    }

    private Field<Double> toQualityCheckedField(String variable) {
        Field<Integer> emepField = field(String.format("%s_EMEP", variable), INTEGER);
        Field<Double> varField = field(variable, FLOAT);
        return case_()
                .when(emepField.eq(2), varField)
                .otherwise(inline(null, varField));
    }

    private Field<Integer> getAggregateFunction(AggregationInterval interval) {
        Field<Integer> timestampDiff = timestampDiff(MINUTE, field("'1990-1-1'", TIMESTAMP), SAMPTIME);
        return floor(timestampDiff.div(interval.getMinutes()));
    }
}
