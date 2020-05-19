package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.VariableMetadataTable.VARIABLE_METADATA;

public class VariableMetadataRecord extends CustomRecord<VariableMetadataRecord> {

    protected VariableMetadataRecord() {
        super(VARIABLE_METADATA);
    }
}
