package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.timeseries.TimeSeriesBuilder;
import fi.csc.avaa.smear.dto.timeseries.TimeSeriesChartBuilder;
import fi.csc.avaa.smear.dto.timeseries.TimeSeriesSheet;
import fi.csc.avaa.smear.dto.timeseries.TimeSeriesSheetBuilder;
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
import org.jooq.SelectSeekStep1;
import org.jooq.Table;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.table.TimeSeriesConstants.CUV_NO;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.SAMPTIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.START_TIME;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_SLOW;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.TABLENAME_HYY_TREE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VALUE;
import static fi.csc.avaa.smear.table.TimeSeriesConstants.VARIABLE;
import static org.jooq.DatePart.MINUTE;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.case_;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.exp;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.keyword;
import static org.jooq.impl.DSL.ln;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.SQLDataType.FLOAT;

@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    DSLContext create;

    @CacheResult(cacheName = "time-series-sheet-cache")
    public TimeSeriesSheet getSheet(TimeSeriesSearch search) {
        return fetch(search, new TimeSeriesSheetBuilder(search.getFrom(), search.getTo(),
                search.getAggregation(), search.getInterval()));
    }

    @CacheResult(cacheName = "time-series-chart-cache")
    public Map<String, List<List<Number>>> getChart(TimeSeriesSearch search) {
        return fetch(search, new TimeSeriesChartBuilder(search.getFrom(), search.getTo(),
                search.getAggregation(), search.getInterval()));
    }

    private <T> T fetch(TimeSeriesSearch search, TimeSeriesBuilder<T> builder) {
        search.getTableToVariables().forEach((tableName, variables) -> {
            if (tableName.equals(TABLENAME_HYY_SLOW)) {
                Result<Record3<LocalDateTime, String, Double>> result = createHyySlowQuery(variables, search).fetch();
                builder.addHyySlowResult(result);
            } else {
                Result<Record> result = createQuery(tableName, variables, search).fetch();
                if (tableName.equals(TABLENAME_HYY_TREE)) {
                    builder.addHyyTreeResult(result, variables);
                } else {
                    builder.addResult(result, tableName, variables);
                }
            }
        });
        return builder.build();
    }

    private Select<Record> createQuery(String tableName, List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = DSL.table(tableName);
        List<SelectFieldOrAsterisk> fields = getFields(variables, search.getQuality(), search.getAggregation());
        Condition conditions = SAMPTIME.greaterOrEqual(search.getFrom())
                .and(SAMPTIME.lessThan(search.getTo()));
        if (tableName.equals(TABLENAME_HYY_TREE)) {
            fields.add(CUV_NO);
            conditions = conditions.and(CUV_NO.in(search.getCuvNos()));
        }
        SelectConditionStep<Record> query = create
                .select(fields)
                .from(table)
                .where(conditions);
        return (search.getAggregation().isGroupedInQuery()
                ? query.groupBy(aggregateFunction(SAMPTIME, search.getInterval()))
                : query)
                .orderBy(SAMPTIME.asc());
    }

    private SelectSeekStep1<Record3<LocalDateTime, String, Double>, LocalDateTime> createHyySlowQuery(
            List<String> variables, TimeSeriesSearch search) {
        Condition startTimeInRange = START_TIME.greaterOrEqual(search.getFrom())
                .and(START_TIME.lessThan(search.getTo()));
        Condition variableNameMatchesSearch = variables
                .stream()
                .map(VARIABLE::eq)
                .reduce(noCondition(), Condition::or);
        SelectConditionStep<Record3<LocalDateTime, String, Double>> query = create
                .select(START_TIME, VARIABLE, VALUE)
                .from(HYY_SLOW)
                .where(startTimeInRange)
                .and(variableNameMatchesSearch);
        return (search.getAggregation().isGroupedInQuery()
                ? query.groupBy(aggregateFunction(START_TIME, search.getInterval()), VARIABLE)
                : query)
                .orderBy(START_TIME.asc());
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
                : field(variable, Double.class);
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
        Field<Integer> emepField = field(String.format("%s_EMEP", variable), Integer.class);
        Field<Double> varField = field(variable, FLOAT);
        return case_()
                .when(emepField.eq(2), varField)
                .otherwise(inline(null, varField));
    }

    private Field<Integer> aggregateFunction(Field<LocalDateTime> to, Integer interval) {
        Field<LocalDateTime> from = field("'1990-1-1'", LocalDateTime.class);
        Field<Integer> timestampDiff = timestampDiff(MINUTE, from, to);
        return floor(timestampDiff.div(interval));
    }

    // https://github.com/jOOQ/jOOQ/issues/4303
    private static Field<Integer> timestampDiff(DatePart part, Field<LocalDateTime> t1, Field<LocalDateTime> t2) {
        return field("timestampdiff({0}, {1}, {2})", Integer.class, keyword(part.toSQL()), t1, t2);
    }
}
