package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.parameter.TimeSeriesSearch;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import org.jooq.DSLContext;
import org.jooq.Query;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@ApplicationScoped
public class TimeSeriesDao {

    @Inject
    MySQLPool client;

    @Inject
    DSLContext create;

    public Uni<List<String>> search(TimeSeriesSearch search) {
        Query query = create
                .select(field("HYY_META.Pamb0"), field("KUM_META.wdir"))
                .from(table("HYY_META"), table("KUM_META"))
                .where();
        return client
                .query(query.getSQL())
                .map(rowSet -> toStream(rowSet)
                        .map(row -> row.getString("HYY_META.wdir"))
                        .collect(Collectors.toList())
                );
    }
}
