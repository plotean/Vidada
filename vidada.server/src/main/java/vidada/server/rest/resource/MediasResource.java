package vidada.server.rest.resource;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.services.IMediaService;

@Path("/medias")
public class MediasResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	/**
	 * Returns all medias
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Collection<MediaItem> getMedias() {
		return mediaService.query(MediaQuery.ALL); 
	}


	/**
	 * Retuns the number of medias
	 * Use vidada/api/medias/count
	 * to get the total number of records
	 * @return
	 */
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = mediaService.query(MediaQuery.ALL).size();
		return String.valueOf(count);
	}
}
