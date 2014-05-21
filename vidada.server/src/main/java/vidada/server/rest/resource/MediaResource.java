package vidada.server.rest.resource;

import vidada.model.media.MediaItem;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Represents a single media resource
 *
 * path: api/medias/{hash}/
 */
public class MediaResource extends AbstractResource {


	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
    private final String mediaHash;

    public MediaResource(String mediaHash){
        this.mediaHash = mediaHash;
    }

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJSON() {
		MediaItem media = mediaService.queryByHash(mediaHash);

        if(media != null){
            return Response.ok(serializeJson(media)).build();
        }else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
	}

    @Path("stream")
    public MediaStreamResource getStream(){
        return new MediaStreamResource(mediaHash);
    }

}
