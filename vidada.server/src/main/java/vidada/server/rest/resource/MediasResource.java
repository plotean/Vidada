package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import vidada.server.rest.VidadaRestServer;
import vidada.services.IMediaService;

@Path("/medias")
public class MediasResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();

	/**
	 * Returns all medias
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public ListPage<MediaItem> getMedias() {
		return mediaService.query(MediaQuery.ALL,0, 5); 
	}

	/*
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public MediaItem getMedias() {
		return mediaService.query(MediaQuery.ALL,0, 5).getPageItems().get(0); 
	}*/


	/**
	 * Retuns the number of medias
	 * Use vidada/api/medias/count
	 * to get the total number of medias
	 * @return
	 */
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {		
		return String.valueOf(mediaService.count());
	}
}
