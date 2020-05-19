package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomRecord;
import org.jooq.impl.CustomTable;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;

public class VariableEventTable extends CustomTable<VariableEventTable.VariableEventRecord> {

    public static final VariableEventTable VARIABLE_EVENT = new VariableEventTable();

    public final TableField<VariableEventRecord, Long> EVENT_ID = createField(name("eventID"), BIGINT);
    public final TableField<VariableEventRecord, Long> VARIABLE_ID = createField(name("variableID"), BIGINT);

    protected VariableEventTable() {
        super(name("variableEvent"));
    }


    @Override
    public Class<? extends VariableEventRecord> getRecordType() {
        return VariableEventRecord.class;
    }

    static class VariableEventRecord extends CustomRecord<VariableEventRecord> {

        protected VariableEventRecord() {
            super(VARIABLE_EVENT);
        }
    }
}
