package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class StationTable extends CustomTable<StationRecord> {

    public static final StationTable STATION = new StationTable();

    public final TableField<StationRecord, Long> ID = createField(name("stationid"), BIGINT);
    public final TableField<StationRecord, String> NAME = createField(name("name"), VARCHAR);
    public final TableField<StationRecord, String> DCMI_POINT = createField(name("dcmipoint"), VARCHAR);

    protected StationTable() {
        super(name("station"));
    }

    @Override
    public Class<? extends StationRecord> getRecordType() {
        return StationRecord.class;
    }
}
