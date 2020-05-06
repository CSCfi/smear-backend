package fi.csc.avaa.smear.resource;

import fi.csc.avaa.smear.constants.Endpoints;
import fi.csc.avaa.smear.dao.EventDao;
import fi.csc.avaa.smear.dto.Event;
import io.smallrye.mutiny.Uni;

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
    public Uni<List<Event>> eventsByVariableIds(@NotNull @NotEmpty @QueryParam("variableId") List<String> variableIds) {
        return dao.findByVariableIds(variableIds);
    }

    @GET
    @Path("/{id}")
    public Uni<Event> event(@NotNull @PathParam("id") Integer id) {
        return dao.findById(id);
    }
}

