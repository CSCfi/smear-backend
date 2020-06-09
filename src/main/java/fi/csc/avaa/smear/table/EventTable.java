package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.LOCALDATE;
import static org.jooq.impl.SQLDataType.LOCALDATETIME;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class EventTable extends CustomTable<EventRecord> {

    public static final EventTable EVENT = new EventTable();

    public final TableField<EventRecord, Long> ID = createField(name("eventID"), BIGINT);
    public final TableField<EventRecord, String> EVENT_TYPE = createField(name("event_type"), VARCHAR);
    public final TableField<EventRecord, String> DESCRIPTION = createField(name("event"), VARCHAR);
    public final TableField<EventRecord, LocalDate> PERIOD_START = createField(name("period_start"), LOCALDATE);
    public final TableField<EventRecord, LocalDate> PERIOD_END = createField(name("period_end"), LOCALDATE);
    public final TableField<EventRecord, LocalDateTime> TIMESTAMP = createField(name("etimestamp"), LOCALDATETIME);

    protected EventTable() {
        super(name("Events"));
    }

    @Override
    public Class<? extends EventRecord> getRecordType() {
        return EventRecord.class;
    }
}
