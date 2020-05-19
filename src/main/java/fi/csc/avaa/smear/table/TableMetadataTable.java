package fi.csc.avaa.smear.table;

import org.jooq.TableField;
import org.jooq.impl.CustomTable;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.SQLDataType.BIGINT;
import static org.jooq.impl.SQLDataType.LOCALDATETIME;
import static org.jooq.impl.SQLDataType.VARCHAR;

public class TableMetadataTable extends CustomTable<TableMetadataRecord> {

    public static final TableMetadataTable TABLE_METADATA = new TableMetadataTable();

    public final TableField<TableMetadataRecord, Long> ID = createField(name("tableID"), BIGINT);
    public final TableField<TableMetadataRecord, Long> STATION_ID = createField(name("stationID"), BIGINT);
    public final TableField<TableMetadataRecord, String> IDENTIFIER = createField(name("identifier"), VARCHAR);
    public final TableField<TableMetadataRecord, String> NAME = createField(name("name"), VARCHAR);
    public final TableField<TableMetadataRecord, String> TITLE = createField(name("title"), VARCHAR);
    public final TableField<TableMetadataRecord, String> SPATIAL_COVERAGE = createField(name("spatial_coverage"), VARCHAR);
    public final TableField<TableMetadataRecord, Long> PERIOD = createField(name("period"), BIGINT);
    public final TableField<TableMetadataRecord, LocalDateTime> TIMESTAMP = createField(name("ttimestamp"), LOCALDATETIME);

    protected TableMetadataTable() {
        super(name("TableMetadata"));
    }

    @Override
    public Class<? extends TableMetadataRecord> getRecordType() {
        return TableMetadataRecord.class;
    }
}
