package vidada.server.rest.resource;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;
import vidada.server.services.IThumbnailService;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;


@Path("/thumbnail")
public class ThumbnailResource extends AbstractResource {

	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	private final IThumbnailService thumbnailService = VidadaRestServer.VIDADA_SERVER.getThumbnailService();

	private static int MIN_SIZE = 50;
	private static int MAX_SIZE = 1000;

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
				System.err.println("Can not recreate thumb of media type: " + media.getType());
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
			e.printStackTrace();
			return Response.status(Status.NOT_FOUND).build();
		}
	}
}
