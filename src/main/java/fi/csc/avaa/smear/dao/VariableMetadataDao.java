package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.DaoUtils.toStream;
import static org.jooq.impl.DSL.field;

@ApplicationScoped
public class VariableMetadataDao {

    @Inject
    MySQLPool client;

    private final String table = "VariableMetadata";
    private final DSLContext create = DSL.using(SQLDialect.MYSQL);

    @CacheResult(cacheName = "variable-metadata-cache")
    public Uni<VariableMetadata> findById(Long id) {
        Query query = create
                .select()
                .from(table)
                .where(field("variableID").eq(id));
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(VariableMetadata::from)
                        .findFirst()
                        .orElseThrow()
                );
    }

    public Uni<List<VariableMetadata>> search(VariableMetadataSearch search) {
        List<Condition> conditions = new ArrayList<>();
        if (!search.variables.isEmpty()) {
            conditions.add(field("variable").in(search.variables));
        }
        if (!search.categories.isEmpty()) {
            conditions.add(field("category").in(search.categories));
        }
        if (!search.tableIds.isEmpty()) {
            conditions.add(field("tableID").in(search.tableIds));
        }
        if (!search.sources.isEmpty()) {
            search.sources.forEach(source ->
                    conditions.add(field("source").like("%" + source + "%")));
        }
        Query query = create
                .select()
                .from(table)
                .where(conditions);
        return client
                .preparedQuery(query.getSQL(), Tuple.tuple(query.getBindValues()))
                .map(rowSet -> toStream(rowSet)
                        .map(VariableMetadata::from)
                        .collect(Collectors.toList())
                );
    }
}
