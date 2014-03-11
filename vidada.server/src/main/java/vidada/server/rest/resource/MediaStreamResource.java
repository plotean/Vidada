package vidada.server.rest.resource;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("/stream")
public class MediaStreamResource extends AbstractResource {
	@GET
	@Path("{hash}")
	public Response getMedia(
			@Context UriInfo uriInfo,
			@PathParam("hash") String hash,
			@QueryParam("mode") String mode) {

		// Streams are handled by a special module outside the REST API

		URI streamLocation = UriBuilder.fromUri(getParent(uriInfo.getBaseUri()))
				.path("stream").path(hash).build();
		//URI streamLocation =  base.resolve("/stream").resolve(hash);
		System.out.println("stream location: " + streamLocation.toString());

		if(mode != null && mode.toUpperCase().equals("LINK")){
			return Response.ok(streamLocation.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}else{
			// REDIRECT (default)
			return Response.seeOther(streamLocation).build();
		}
	} 
}
