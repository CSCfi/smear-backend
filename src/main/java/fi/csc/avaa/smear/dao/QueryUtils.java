package fi.csc.avaa.smear.dao;

import org.jooq.Condition;

import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
import static org.jooq.impl.DSL.noCondition;

public final class QueryUtils {

    protected static Condition toTableAndVariableConditions(Map<String, List<String>> tableToVariables) {
        return tableToVariables.entrySet()
                .stream()
                .map(entry -> TABLE_METADATA.NAME.eq(entry.getKey())
                        .and(VARIABLE_METADATA.NAME.in(entry.getValue())))
                .reduce(noCondition(), Condition::or);
    }
}
