package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.EventTable.EVENT;

public class EventRecord extends CustomRecord<EventRecord> {

    protected EventRecord() {
        super(EVENT);
    }
}
