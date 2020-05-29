package fi.csc.avaa.smear.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SmearResource {

    @GET
    @Path("/hello")
    public String hello() {
        return "hello";
    }
}
