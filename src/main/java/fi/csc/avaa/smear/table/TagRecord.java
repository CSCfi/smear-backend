package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.TagTable.TAG;

public class TagRecord extends CustomRecord<TagRecord> {

    protected TagRecord() {
        super(TAG);
    }
}
