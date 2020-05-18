package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.BOOLEAN;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.SQLDataType.LOCALDATETIME;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class VariableMetadataTable extends CustomTable<VariableMetadataRecord> {

    public static final VariableMetadataTable VARIABLE_METADATA = new VariableMetadataTable();

    public final TableField<VariableMetadataRecord, Long> ID = createField(name("variableID"), BIGINT);
    public final TableField<VariableMetadataRecord, Long> TABLE_ID = createField(name("tableID"), BIGINT);
    public final TableField<VariableMetadataRecord, String> NAME = createField(name("variable"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> DESCRIPTION = createField(name("description"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> TYPE = createField(name("type"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> UNIT = createField(name("unit"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> TITLE = createField(name("title"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> SOURCE = createField(name("source"), VARCHAR);
    // MySQL accepts datetimes with month/day set to 0 and we have such values in the database
    // LocalDateTimes cannot be constructed from these rows so we treat them as strings
    public final TableField<VariableMetadataRecord, String> PERIOD_START = createField(name("period_start"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> PERIOD_END = createField(name("period_end"), VARCHAR);
    public final TableField<VariableMetadataRecord, Integer> COVERAGE = createField(name("coverage"), INTEGER);
    public final TableField<VariableMetadataRecord, String> RIGHTS = createField(name("rights"), VARCHAR);
    public final TableField<VariableMetadataRecord, String> CATEGORY = createField(name("category"), VARCHAR);
    public final TableField<VariableMetadataRecord, Boolean> MANDATORY = createField(name("mandatory"), BOOLEAN);
    public final TableField<VariableMetadataRecord, Boolean> DERIVATIVE = createField(name("derivative"), BOOLEAN);
    public final TableField<VariableMetadataRecord, Integer> UI_SORT_ORDER = createField(name("ui_sort_order"), INTEGER);
    public final TableField<VariableMetadataRecord, String> UI_AVG_TYPE = createField(name("ui_avg_type"), VARCHAR);
    public final TableField<VariableMetadataRecord, LocalDateTime> TIMESTAMP = createField(name("vtimestamp"), LOCALDATETIME);

    protected VariableMetadataTable() {
        super(name("VariableMetadata"));
    }

    @Override
    public Class<? extends VariableMetadataRecord> getRecordType() {
        return VariableMetadataRecord.class;
    }
}
