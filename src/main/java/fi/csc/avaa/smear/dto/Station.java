package fi.csc.avaa.smear.dto;

import io.vertx.mutiny.sqlclient.Row;

public class Station {

    public int stationId;
    public String name;

    public static Station from(Row row) {
        Station station = new Station();
        station.stationId = row.getInteger("stationid");
        station.name = row.getString("name");
        return station;
    }
}
