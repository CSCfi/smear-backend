package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.TableMetadataTable.TABLE_METADATA;

public class TableMetadataRecord extends CustomRecord<TableMetadataRecord> {

    protected TableMetadataRecord() {
        super(TABLE_METADATA);
    }
}
