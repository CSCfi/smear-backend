package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.dao.EventDao;
import fi.csc.avaa.smear.dto.Event;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path(Endpoints.EVENTS)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    @Inject
    EventDao dao;

    @GET
    @Operation(
            summary = "Fetch events by variable id",
            description = "Returns a list of events linked to a variable. " +
                    "Events describe changes in time series data. The event types in the SMEAR database are " +
                    "based on DDI data lifecycle events and ENVRI reference model. The time range of the event " +
                    "indicates the time frame when the event affects the data."
    )
    public List<Event> eventsByVariableIds(
            @NotNull
            @NotEmpty
            @Parameter(description = "Unique id of a variable. " +
                    "Multiple parameters can be used and at least one is required.",
                    example = "43")
            @QueryParam("variableId") List<Long> variableIds
    ) {
        return dao.findByVariableIds(variableIds);
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Fetch single event by event id",
            description = "Events describe changes in time series data. The event types in the SMEAR database are " +
                    "based on DDI data lifecycle events and ENVRI reference model. The time range of the event " +
                    "indicates the time frame when the event affects the data."
    )
    public Event event(
            @NotNull
            @Parameter(description = "Unique id of an event.", example = "201")
            @PathParam("id") Long id
    ) {
        return dao.findById(id);
    }
}

