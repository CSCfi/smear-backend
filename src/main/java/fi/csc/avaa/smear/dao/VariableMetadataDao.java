package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.quarkus.cache.CacheResult;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Select;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.lower;
import static org.jooq.impl.SQLDataType.VARCHAR;

@ApplicationScoped
public class VariableMetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, VariableMetadata> recordToVariableMetadata = record ->
            VariableMetadata.builder()
                    .id(record.get(field("variableID"), Long.class))
                    .tableId(record.get(field("tableID"), Long.class))
                    .name(record.get(field("variable"), String.class))
                    .description(record.get(field("description"), String.class))
                    .type(record.get(field("type"), String.class))
                    .unit(record.get(field("unit"), String.class))
                    .title(record.get(field("title"), String.class))
                    .source(record.get(field("source"), String.class))
                    .periodStart(record.get(field("period_start"), LocalDateTime.class))
                    .periodEnd(record.get(field("period_end"), LocalDateTime.class))
                    .coverage(record.get(field("coverage"), Integer.class))
                    .rights(record.get(field("rights"), String.class))
                    .category(record.get(field("category"), String.class))
                    .mandatory(record.get(field("mandatory"), Boolean.class))
                    .derivative(record.get(field("derivative"), Boolean.class))
                    .uiSortOrder(record.get(field("ui_sort_order"), Integer.class))
                    .uiAvgType(record.get(field("ui_avg_type"), String.class))
                    .timestamp(record.get(field("vtimestamp"), LocalDateTime.class))
                    .build();

    @CacheResult(cacheName = "variable-metadata-cache")
    public VariableMetadata findById(Long id) {
        return create
                .select()
                .from("VariableMetadata")
                .where(field("variableID").eq(id))
                .fetchOne(recordToVariableMetadata);
    }

    public List<VariableMetadata> search(VariableMetadataSearch search) {
        Select<Record> query = search.tablevariables.isEmpty()
                ? getSearchQuery(search)
                : getTableVariableQuery(search);
        return query.fetch(recordToVariableMetadata);
    }

    private Select<Record> getSearchQuery(VariableMetadataSearch search) {
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
