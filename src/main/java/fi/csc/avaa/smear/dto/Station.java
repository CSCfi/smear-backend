package fi.csc.avaa.smear.dto;

import org.jooq.Record;

import static org.jooq.impl.DSL.field;

public class Station {

    public int id;
    public String name;

    public static Station from(Record record) {
        Station station = new Station();
        station.id = record.get(field("stationid"), Integer.class);
        station.name = record.get(field("name"), String.class);
        return station;
    }
}
