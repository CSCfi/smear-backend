package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import io.quarkus.cache.CacheResult;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TableField;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fi.csc.avaa.smear.dao.QueryUtils.toTableAndVariableConditions;
import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
import static org.jooq.impl.DSL.noCondition;

@ApplicationScoped
public class VariableMetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<Record, VariableMetadata> recordToVariableMetadata = record ->
            VariableMetadata.builder()
                    .id(record.get(VARIABLE_METADATA.ID))
                    .tableId(record.get(VARIABLE_METADATA.TABLE_ID))
                    .tableName(record.get(TABLE_METADATA.NAME))
                    .name(record.get(VARIABLE_METADATA.NAME))
                    .description(record.get(VARIABLE_METADATA.DESCRIPTION))
                    .type(record.get(VARIABLE_METADATA.TYPE))
                    .unit(record.get(VARIABLE_METADATA.UNIT))
                    .title(record.get(VARIABLE_METADATA.TITLE))
                    .source(record.get(VARIABLE_METADATA.SOURCE))
                    .periodStart(record.get(VARIABLE_METADATA.PERIOD_START).replace(' ', 'T'))
                    .periodEnd(Optional.ofNullable(record.get(VARIABLE_METADATA.PERIOD_END))
                            .map(str -> str.replace(' ', 'T'))
                            .orElse(null))
                    .coverage(record.get(VARIABLE_METADATA.COVERAGE))
                    .rights(record.get(VARIABLE_METADATA.RIGHTS))
                    .category(record.get(VARIABLE_METADATA.CATEGORY))
                    .mandatory(record.get(VARIABLE_METADATA.MANDATORY))
                    .derivative(record.get(VARIABLE_METADATA.DERIVATIVE))
                    .uiSortOrder(record.get(VARIABLE_METADATA.UI_SORT_ORDER))
                    .uiAvgType(record.get(VARIABLE_METADATA.UI_AVG_TYPE))
                    .timestamp(record.get(VARIABLE_METADATA.TIMESTAMP))
                    .build();

    public VariableMetadata findById(Long variableId) {
        return create
                .select()
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA)
                .on(TABLE_METADATA.ID.eq(VARIABLE_METADATA.TABLE_ID))
                .where(VARIABLE_METADATA.ID.eq(variableId))
                .fetchOne(recordToVariableMetadata);
    }

    @CacheResult(cacheName = "variable-metadata-by-table-cache")
    public List<VariableMetadata> findByTableId(Long tableId) {
        return create
                .select()
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA)
                .on(TABLE_METADATA.ID.eq(VARIABLE_METADATA.TABLE_ID))
                .where(VARIABLE_METADATA.TABLE_ID.eq(tableId))
                .fetch(recordToVariableMetadata);
    }

    @CacheResult(cacheName = "variable-metadata-findall-cache")
    public List<VariableMetadata> findAll() {
        return create
                .select()
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA)
                .on(TABLE_METADATA.ID.eq(VARIABLE_METADATA.TABLE_ID))
                .fetch(recordToVariableMetadata);
    }

    @CacheResult(cacheName = "variable-metadata-search-cache")
    public List<VariableMetadata> search(VariableMetadataSearch search) {
        Condition conditions = search.getTableToVariables().isEmpty()
                ? toSearchConditions(search)
                : toTableAndVariableConditions(search.getTableToVariables());
        return create
                .select()
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA)
                .on(TABLE_METADATA.ID.eq(VARIABLE_METADATA.TABLE_ID))
                .where(conditions)
                .fetch(recordToVariableMetadata);
    }

    private Condition toSearchConditions(VariableMetadataSearch search) {
        List<Condition> conditions = new ArrayList<>();
        if (!search.getVariables().isEmpty()) {
            conditions.add(VARIABLE_METADATA.NAME.in(search.getVariables()));
        }
        if (!search.getTables().isEmpty()) {
            conditions.add(TABLE_METADATA.NAME.in(search.getTables()));
        }
        if (!search.getCategories().isEmpty()) {
            conditions.add(toTextSearchConditions(search.getCategories(), VARIABLE_METADATA.CATEGORY));
        }
        if (!search.getDescriptions().isEmpty()) {
            conditions.add(toTextSearchConditions(search.getDescriptions(), VARIABLE_METADATA.DESCRIPTION));
        }
        if (!search.getSources().isEmpty()) {
            conditions.add(toTextSearchConditions(search.getSources(), VARIABLE_METADATA.SOURCE));
        }
        return conditions
                .stream()
                .reduce(noCondition(), Condition::and);
    }

    private Condition toTextSearchConditions(List<String> searchStrings,
                                             TableField<? extends Record, String> field) {
        return searchStrings
                .stream()
                .map(field::containsIgnoreCase)
                .reduce(noCondition(), Condition::or);
    }
}
