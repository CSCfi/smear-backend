package fi.csc.avaa.smear.dao;

import org.jooq.Condition;

import java.util.List;
import java.util.Map;

import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;
import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;
import static org.jooq.impl.DSL.noCondition;

public final class Conditions {

    protected static final Condition VARIABLE_IS_PUBLIC = VARIABLE_METADATA.RIGHTS.eq("public");

    protected static Condition tableAndVariableMatches(Map<String, List<String>> tableToVariables) {
        return tableToVariables.entrySet()
                .stream()
                .map(entry -> TABLE_METADATA.NAME.eq(entry.getKey())
                        .and(VARIABLE_METADATA.NAME.in(entry.getValue())))
                .reduce(noCondition(), Condition::or);
    }
}
