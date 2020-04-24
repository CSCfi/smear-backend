package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidIsoDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.List;

public class TimeSeriesParameters {

    @QueryParam("variables")
    @NotNull
    @NotEmpty
    public List<String> variables;

    @QueryParam("table")
    @NotNull
    @NotEmpty
    public String table;

    @QueryParam("tablevariables")
    public List<String> tablevariables;

    @QueryParam("from")
    @NotNull
    @ValidIsoDate
    public String from;

    @QueryParam("to")
    @NotNull
    @ValidIsoDate
    public String to;

    @QueryParam("quality")
    @NotNull
    @NotEmpty
    public String quality;

    @QueryParam("averaging")
    @NotNull
    @NotEmpty
    public String averaging;

    @QueryParam("type")
    @NotNull
    @NotEmpty
    public String type;

    @QueryParam("cuv_no")
    @NotNull
    @NotEmpty
    public String cuv_no;

    public LocalDateTime getFromDateTime() {
        return LocalDateTime.parse(from);
    }

    public LocalDateTime getToDateTime() {
        return LocalDateTime.parse(to);
    }
}
