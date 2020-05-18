package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.MetadataTable.METADATA;

public class MetadataRecord extends CustomRecord<MetadataRecord> {

    protected MetadataRecord() {
        super(METADATA);
    }
}
