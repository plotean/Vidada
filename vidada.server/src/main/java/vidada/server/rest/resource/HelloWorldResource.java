package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("")
public class HelloWorldResource {

	@GET
	@Produces("text/html")
	public String getMessage(){
		return "Welcome to Vidada REST Server!";
	}
}
