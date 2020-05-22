package fi.csc.avaa.smear.dao;

import fi.csc.avaa.smear.dto.VariableMetadata;
import fi.csc.avaa.smear.parameter.VariableMetadataSearch;
import fi.csc.avaa.smear.table.VariableMetadataRecord;
import io.quarkus.cache.CacheResult;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.lower;

@ApplicationScoped
public class VariableMetadataDao {

    @Inject
    DSLContext create;

    private final RecordMapper<VariableMetadataRecord, VariableMetadata> recordToVariableMetadata = record ->
            VariableMetadata.builder()
                    .id(record.get(VARIABLE_METADATA.ID))
                    .tableId(record.get(VARIABLE_METADATA.TABLE_ID))
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

    @CacheResult(cacheName = "variable-metadata-findall-cache")
    public List<VariableMetadata> findAll() {
        return create
                .selectFrom(VARIABLE_METADATA)
                .fetch(recordToVariableMetadata);
    }

    @CacheResult(cacheName = "variable-metadata-cache")
    public VariableMetadata findByVariableId(Long id) {
        return create
                .selectFrom(VARIABLE_METADATA)
                .where(VARIABLE_METADATA.ID.eq(id))
                .fetchOne(recordToVariableMetadata);
    }

    @CacheResult(cacheName = "variable-metadata-search-cache")
    public List<VariableMetadata> search(VariableMetadataSearch search) {
        return search.getTableToVariable().isEmpty()
                ? findBy(search)
                : findByTableVariables(search.getTableToVariable());
    }

    private List<VariableMetadata> findBy(VariableMetadataSearch search) {
        List<Condition> conditions = new ArrayList<>();
        if (!search.getVariableIds().isEmpty()) {
            conditions.add(VARIABLE_METADATA.ID.in(search.getVariableIds()));
        }
        if (!search.getVariables().isEmpty()) {
            conditions.add(VARIABLE_METADATA.NAME.in(search.getVariables()));
        }
        if (!search.getCategories().isEmpty()) {
            conditions.add(VARIABLE_METADATA.CATEGORY.in(search.getCategories()));
        }
        if (!search.getTableIds().isEmpty()) {
            conditions.add(VARIABLE_METADATA.TABLE_ID.in(search.getTableIds()));
        }
        search.getSources().forEach(source ->
                conditions.add(lower(VARIABLE_METADATA.SOURCE)
                        .like("%" + source.toLowerCase() + "%")));
        return create
                .selectFrom(VARIABLE_METADATA)
                .where(conditions)
                .fetch(recordToVariableMetadata);
    }

    private List<VariableMetadata> findByTableVariables(Map<String, String> tableToVariable) {
        Condition conditions = tableToVariable.entrySet()
                .stream()
                .map(entry -> field("TableMetadata.name").eq(entry.getKey())
                        .and(field("VariableMetadata.variable").eq(entry.getValue())))
                .reduce(DSL.noCondition(), Condition::or);
        return create
                .select()
                .from(VARIABLE_METADATA)
                .join(TABLE_METADATA)
                .on(TABLE_METADATA.ID.eq(VARIABLE_METADATA.TABLE_ID))
                .where(conditions)
                .fetchInto(VARIABLE_METADATA)
                .map(recordToVariableMetadata);
    }
}
