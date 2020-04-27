package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static fi.csc.avaa.smear.dao.Utils.toListArg;
import static fi.csc.avaa.smear.dao.Utils.toStream;

@ApplicationScoped
public class VariableMetadataDao {

    @Inject
    MySQLPool client;

    @CacheResult(cacheName = "table-metadata-cache")
    public Uni<VariableMetadata> findById(Long id) {
        return client
                .preparedQuery("SELECT * FROM VariableMetadata WHERE variableID = ?", Tuple.of(id))
                .map(rowSet -> toStream(rowSet)
                        .map(VariableMetadata::from)
                        .findFirst()
                        .orElseThrow()
                );
    }

    public Uni<List<VariableMetadata>> search(VariableMetadataSearch search) {
        List<String> conditions = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();
        if (!search.variables.isEmpty()) {
            conditions.add("variable IN (?)");
            arguments.add(toListArg(search.variables));
        }
        if (!search.categories.isEmpty()) {
            conditions.add("category IN (?)");
            arguments.add(toListArg(search.variables));
        }
        if (!search.sources.isEmpty()) {
            conditions.add("source IN (?)");
            arguments.add(toListArg(search.variables));
        }
        if (!search.tableIds.isEmpty()) {
            conditions.add("tableID IN (?)");
            arguments.add(toListArg(search.variables));
        }
        String query = "SELECT * FROM VariableMetadata WHERE " + String.join(" AND ", conditions);
        return client
                .preparedQuery(query, Tuple.tuple(arguments))
                .map(rowSet -> toStream(rowSet)
                        .map(VariableMetadata::from)
                        .collect(Collectors.toList())
                );
    }
}
