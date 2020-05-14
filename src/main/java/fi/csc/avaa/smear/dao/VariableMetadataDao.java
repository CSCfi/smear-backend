package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.quarkus.cache.CacheResult;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.lower;
import static org.jooq.impl.SQLDataType.VARCHAR;

@ApplicationScoped
public class VariableMetadataDao {


    @Inject
    DataSource smearDataSource;



    @CacheResult(cacheName = "variable-metadata-cache")
    public VariableMetadata findById(Long id) {
        DSLContext create = DSL.using(smearDataSource, SQLDialect.MYSQL);
        return create
                .select()
                .from("VariableMetadata")
                .where(field("variableID").eq(id))
                .fetchOne(VariableMetadata::from);
    }

    public List<VariableMetadata> search(VariableMetadataSearch search) {
        Select<Record> query = search.tablevariables.isEmpty()
                ? getSearchQuery(search)
                : getTableVariableQuery(search);
        return query.fetch(VariableMetadata::from);
    }

    private Select<Record> getSearchQuery(VariableMetadataSearch search) {
        DSLContext create = DSL.using(smearDataSource, SQLDialect.MYSQL);
        List<Condition> conditions = new ArrayList<>();
        if (!search.variableIds.isEmpty()) {
            conditions.add(field("variableID").in(search.variableIds));
        }
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
                    conditions.add(lower(field("source", VARCHAR))
                            .like("%" + source.toLowerCase() + "%")));
        }
        return create
                .select()
                .from("VariableMetadata")
                .where(conditions);
    }

    private Select<Record> getTableVariableQuery(VariableMetadataSearch search) {
        DSLContext create = DSL.using(smearDataSource, SQLDialect.MYSQL);
        Condition conditions = search.getTableToVariable().entrySet()
                .stream()
                .map(entry -> field("TableMetadata.name").eq(entry.getKey())
                        .and(field("VariableMetadata.variable").eq(entry.getValue())))
                .reduce(DSL.noCondition(), Condition::or);
        return create
                .select()
                .from("VariableMetadata")
                .join("TableMetadata")
                .on(field("TableMetadata.tableID").eq(field("VariableMetadata.tableID")))
                .where(conditions);
    }
}
