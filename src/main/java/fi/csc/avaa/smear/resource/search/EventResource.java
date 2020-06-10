package fi.csc.avaa.smear.resource.search;

import fi.csc.avaa.smear.config.Endpoints;
import fi.csc.avaa.smear.dao.EventDao;
import fi.csc.avaa.smear.dto.Event;
import fi.csc.avaa.smear.parameter.ParameterUtils;
import fi.csc.avaa.smear.validation.Patterns;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path(Endpoints.SEARCH_EVENTS)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventDao dao;

    @GET
    @Operation(
            summary = "Fetch events by variable name",
            description = "Returns a list of events linked to a variable. " +
                    "Events describe changes in time series data. The event types in the SMEAR database are " +
                    "based on DDI data lifecycle events and ENVRI reference model. The time range of the event " +
                    "indicates the time frame when the event affects the data."
    )
    public List<Event> search(
            @Parameter(description = "Name of a table and a variable separated by a period. " +
                    "Table and variable names can be queried from the table and variable metadata endpoints. " +
                    "Multiple parameters can be used and at least one is required.",
                    example = "HYY_META.Pamb0")
            @NotNull
            @NotEmpty
            @QueryParam("tablevariable") List<@NotEmpty @Pattern(regexp = Patterns.TABLEVARIABLE) String> tablevariable
    ) {
        Map<String, List<String>> tableToVariables = ParameterUtils.mapTablesToVariables(tablevariable);
        return dao.findByTablesAndVariables(tableToVariables);
    }
}

