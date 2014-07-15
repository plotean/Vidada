package vidada.server.rest.resource;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;
import vidada.server.services.IThumbnailService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;


@Path("/thumbnail")
public class ThumbnailResource extends AbstractResource {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(ThumbnailResource.class.getName());

    private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	private final IThumbnailService thumbnailService = VidadaRestServer.VIDADA_SERVER.getThumbnailService();

	private static int MIN_SIZE = 50;
	private static int MAX_SIZE = 1000;


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    public String getDefault(){
        return "REST Resource which provides access to media thumbnails.";
    }

    @POST
	@Path("{hash}")
	public Response updateThumb(
			@PathParam("hash") String hash,
			float pos){

		MediaItem media = mediaService.queryByHash(hash);
		if(media != null){
			if(media instanceof MovieMediaItem){
				thumbnailService.renewThumbImage((MovieMediaItem)media, pos);
				return Response.status(Status.ACCEPTED).build();
			}else{
                logger.error("Can not recreate thumb of media type: " + media.getType());
				Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
			}
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	@GET
	@Path("{hash}")
	@Produces("image/png")
	public Response getPNG(@PathParam("hash") String hash,
			@QueryParam("width") int width,
			@QueryParam("height") int height) {
		MediaItem media = mediaService.queryByHash(hash);

		width = Math.max(width, MIN_SIZE);
		height = Math.max(height, MIN_SIZE);
		width = Math.min(width, MAX_SIZE);
		height = Math.min(height, MAX_SIZE);

		Size thumbSize = new Size(width, height);

		try {
			final IMemoryImage image = thumbnailService.getThumbImage(media, thumbSize);
            if(image != null) {
                StreamingOutput imageStream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        image.writePNG(output);
                    }
                };
                return Response.ok(imageStream).build();
            }else{
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
		} catch (Exception e) {
            logger.error(e);
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}
