package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.TimeSeriesBuilder;
import fi.csc.avaa.smear.parameter.Aggregation;
import fi.csc.avaa.smear.parameter.Quality;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.table.TimeSeriesConstants.COL_CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COL_SAMPTIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COL_START_TIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COL_VALUE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.COL_VARIABLE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLE_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLE_HYY_TREE;
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
  compare responses with production version, fix timestamps
 */
@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    DSLContext create;

    private static final Field<Timestamp> SAMPTIME = field(COL_SAMPTIME, TIMESTAMP);

    @CacheResult(cacheName = "time-series-search-cache")
    public Map<String, Map<String, Object>> search(TimeSeriesSearch search) {
        TimeSeriesBuilder builder = new TimeSeriesBuilder(search.getAggregation(), search.aggregationInterval);
        search.getTableToVariables().forEach((tableName, variables) -> {
            if (tableName.equals(TABLE_HYY_SLOW)) {
                Select<Record3<Timestamp, String, Double>> query = createHyySlowQuery(variables, search);
                Result<Record3<Timestamp, String, Double>> result = query.fetch();
                builder.addHyySlowResult(result);
            } else {
                Select<Record> query = createQuery(tableName, variables, search);
                Result<Record> result = query.fetch();
                if (tableName.equals(TABLE_HYY_TREE)) {
                    builder.addHyyTreeResult(result, variables);
                } else {
                    builder.addResult(result, tableName, variables);
                }
            }
        });
        return builder.build();
    }

    private Select<Record> createQuery(String tableName, List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = table(tableName);
        List<SelectFieldOrAsterisk> fields = getFields(variables, search.getQuality(), search.getAggregation());
        Condition conditions = SAMPTIME.between(search.getFromTimestamp(), search.getToTimestamp());
        if (tableName.equals(TABLE_HYY_TREE)) {
            Field<Integer> cuvNo = field(COL_CUV_NO, INTEGER);
            fields.add(cuvNo);
            conditions = conditions.and(cuvNo.in(search.getCuvNos()));
        }
        Field<Integer> aggregateFunction = getAggregateFunction(SAMPTIME, search.aggregationInterval);
        SelectConditionStep<Record> query = create
                .select(fields)
                .from(table)
                .where(conditions);
        return (search.getAggregation().isGroupedInQuery()
                ? query.groupBy(aggregateFunction)
                : query)
                .orderBy(SAMPTIME.asc());
    }

    private Select<Record3<Timestamp, String, Double>> createHyySlowQuery(
            List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = table(TABLE_HYY_SLOW);
        Field<Timestamp> startTime = field(COL_START_TIME, TIMESTAMP);
        Field<String> variableName = field(COL_VARIABLE, VARCHAR);
        Field<Double> value = field(COL_VALUE, FLOAT);
        Condition startTimeInRange = startTime.between(search.getFromTimestamp(), search.getToTimestamp());
        Field<Integer> aggregateFunction = getAggregateFunction(startTime, search.aggregationInterval);
        Condition conditions = variables
                .stream()
                .map(variableName::eq)
                .reduce(noCondition(), Condition::or);
        SelectConditionStep<Record3<Timestamp, String, Double>> query = create
                .select(startTime, variableName, value)
                .from(table)
                .where(startTimeInRange)
                .and(conditions);
        return (search.getAggregation().isGroupedInQuery()
                ? query.groupBy(aggregateFunction, variableName)
                : query)
                .orderBy(startTime.asc());
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

    private Field<Integer> getAggregateFunction(Field<Timestamp> to, Integer interval) {
        Field<Timestamp> from = field("'1990-1-1'", TIMESTAMP);
        Field<Integer> timestampDiff = timestampDiff(MINUTE, from, to);
        return floor(timestampDiff.div(interval));
    }

    // https://github.com/jOOQ/jOOQ/issues/4303
    private static Field<Integer> timestampDiff(DatePart part, Field<Timestamp> t1, Field<Timestamp> t2) {
        return DSL.field("timestampdiff({0}, {1}, {2})", Integer.class, DSL.keyword(part.toSQL()), t1, t2);
    }
}
