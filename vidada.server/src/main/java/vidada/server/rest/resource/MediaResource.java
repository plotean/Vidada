package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import vidada.model.media.MediaItem;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;

@Path("/media")
public class MediaResource {


	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();


	@GET
	@Path("{hash}")
	@Produces({ MediaType.APPLICATION_JSON })
	public MediaItem getJSON(@PathParam("hash") String hash) {
		MediaItem media = mediaService.queryByHash(hash);
		return media;
	}

}
