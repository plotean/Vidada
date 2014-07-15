package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Ping-Resource which simply returns "success".
 * Useful for clients to check connection.
 */
@Path("ping")
public class PingResource extends AbstractResource {

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public String ping() {
        return "success";
    }
}
