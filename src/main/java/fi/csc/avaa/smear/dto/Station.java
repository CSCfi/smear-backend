package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

public class Station {

    public int id;
    public String name;

    public static Station from(Row row) {
        Station station = new Station();
        station.id = row.getInteger("stationid");
        station.name = row.getString("name");
        return station;
    }
}
