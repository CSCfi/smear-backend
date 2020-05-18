package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomRecord;
import org.jooq.impl.CustomTable;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;

public class VariableTagTable extends CustomTable<VariableTagTable.VariableTagRecord> {

    public static final VariableTagTable VARIABLE_TAG = new VariableTagTable();

    public final TableField<VariableTagRecord, Long> TAG_ID = createField(name("TagID"), BIGINT);
    public final TableField<VariableTagRecord, Long> VARIABLE_ID = createField(name("variableID"), BIGINT);

    protected VariableTagTable() {
        super(name("variableTags"));
    }


    @Override
    public Class<? extends VariableTagRecord> getRecordType() {
        return VariableTagRecord.class;
    }

    static class VariableTagRecord extends CustomRecord<VariableTagRecord> {

        protected VariableTagRecord() {
            super(VARIABLE_TAG);
        }
    }
}
