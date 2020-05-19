package fi.csc.avaa.smear.table;

import org.jooq.impl.CustomRecord;

import static fi.csc.avaa.smear.table.StationTable.STATION;

public class StationRecord extends CustomRecord<StationRecord> {

    protected StationRecord() {
        super(STATION);
    }
}
