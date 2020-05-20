package fi.csc.avaa.smear.dto;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormat {

    public static final String ISO8601_DATETIME_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final DateTimeFormatter ISO8601_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(ISO8601_DATETIME_WITH_MILLIS);
}
