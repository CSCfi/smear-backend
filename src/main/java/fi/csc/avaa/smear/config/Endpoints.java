package fi.csc.avaa.smear.config;

public final class Endpoints {

    public static final String CLASSIFICATIONS = "/classifications";

    public static final String STATIONS = "/station";
    public static final String STATION = STATIONS + "/{stationId}";
    public static final String TABLES = STATION + "/table";
    public static final String TABLE = TABLES + "/{tableId}";
    public static final String VARIABLES = TABLE + "/variable";
    public static final String VARIABLE = VARIABLES + "/{variableId}";

    private static final String SEARCH = "/search";
    public static final String SEARCH_EVENTS = SEARCH + "/event";
    public static final String SEARCH_METADATA = SEARCH + "/metadata";
    public static final String SEARCH_STATIONS = SEARCH + "/station";
    public static final String SEARCH_TABLES = SEARCH + "/table";
    public static final String SEARCH_TAGS = SEARCH + "/tag";
    public static final String SEARCH_TIMESERIES = SEARCH + "/timeseries";
    public static final String SEARCH_VARIABLES = SEARCH + "/variable";
}
