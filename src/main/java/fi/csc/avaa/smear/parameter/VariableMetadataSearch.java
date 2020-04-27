package fi.csc.avaa.smear.parameter;

import fi.csc.avaa.smear.validation.ValidVariableMetadataSearch;

import javax.ws.rs.QueryParam;
import java.util.List;

@ValidVariableMetadataSearch
public class VariableMetadataSearch {

    @QueryParam("variable")
    public List<String> variables;

    @QueryParam("tablevariable")
    public List<String> tablevariables;

    @QueryParam("category")
    public List<String> categories;

    @QueryParam("source")
    public List<String> sources;

    @QueryParam("table_id")
    public List<String> tableIds;
}
