package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.constants.AggregationType;
import fi.csc.avaa.smear.constants.Quality;
import fi.csc.avaa.smear.dto.TimeSeriesBuilder;
import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.quarkus.cache.CacheResult;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.Condition;
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
import static org.jooq.impl.DSL.case_;
import static org.jooq.impl.DSL.exp;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.floor;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.ln;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.min;
import static org.jooq.impl.DSL.sum;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.SQLDataType.FLOAT;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.SQLDataType.TIMESTAMP;

/*
 TODO:
  HYY_TREE special case
  HYYSLOWQueries
  AvailabilityQueries
  Table name validation (must exist)
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
        search.getTableToVariables().forEach((tableName, variables) -> {
            Query query = createQuery(tableName, variables, search);
            client.preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                    .map(DaoUtils::toStream)
                    .await().indefinitely()
                    .forEach(row -> builder.add(row, tableName, variables));
        });
        return builder.build();
    }

    private Query createQuery(String tableName, List<String> variables, TimeSeriesSearch search) {
        Table<Record> table = table(tableName);
        List<SelectFieldOrAsterisk> fields = getFields(variables, search.getQuality(), search.getAggregationType());
        Condition samptimeInRange = SAMPTIME.between(search.getFromTimestamp(), search.getToTimestamp());
        SelectConditionStep<Record> baseQuery = create
                .select(fields)
                .from(table)
                .where(samptimeInRange);
        if (search.getAggregationType().isGroupedByInterval()) {
            Field<Integer> timestampDiff = timestampDiff(MINUTE, field("'1990-1-1'", TIMESTAMP), SAMPTIME);
            int interval = search.getAggregationInterval().getMinutes();
            return baseQuery.groupBy(floor(timestampDiff.div(interval)));
        } else {
            return baseQuery;
        }
    }

    private List<SelectFieldOrAsterisk> getFields(List<String> variables, Quality quality,
                                                  AggregationType aggregationType) {
        List<SelectFieldOrAsterisk> fields = new ArrayList<>();
        fields.add(SAMPTIME);
        fields.addAll(variables
                .stream()
                .map(variable -> toField(variable, quality, aggregationType)
                        .as(variable))
                .collect(Collectors.toList()));
        return fields;
    }

    private Field<? extends Number> toField(String variable, Quality quality, AggregationType aggregationType) {
        Field<Double> field = quality.equals(Quality.CHECKED)
                ? toQualityCheckedField(variable)
                : field(variable, FLOAT);
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

    private Field<Double> toQualityCheckedField(String variable) {
        Field<Integer> emepField = field(String.format("%s_EMEP", variable), INTEGER);
        Field<Double> varField = field(variable, FLOAT);
        return case_()
                .when(emepField.eq(2), varField)
                .otherwise(inline(null, varField));
    }
}
