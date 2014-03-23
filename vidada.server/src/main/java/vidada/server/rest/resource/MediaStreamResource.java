package vidada.server.rest.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/stream")
public class MediaStreamResource extends AbstractResource {
	@GET
	@Path("{hash}")
	public Response getMedia(
			@Context UriInfo uriInfo,
			@PathParam("hash") String hash,
			@QueryParam("redirect") @DefaultValue("true") boolean redirect) {

		// Streams are handled by a special module outside the REST API

		URI streamLocation = UriBuilder.fromUri(getParent(uriInfo.getBaseUri()))
				.path("stream").path(hash).build();

		if(redirect){
            // REDIRECT (default)
            return Response.seeOther(streamLocation).build();
		}else{
            // Just return a link
            return Response.ok(streamLocation.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
	} 
}
