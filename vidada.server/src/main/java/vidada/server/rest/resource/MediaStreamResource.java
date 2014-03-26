package vidada.server.rest.resource;

import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

@Path("/stream")
public class MediaStreamResource extends AbstractResource {

    private static final boolean APPEND_ORIGINAL_NAME = false;
    private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();

    @GET
    @Path("{hash}")
    public Response getMedia(
            @Context UriInfo uriInfo,
            @PathParam("hash") String hash,
            @QueryParam("redirect") @DefaultValue("true") boolean redirect) {

        // Streams are handled by a special module outside the REST API

        String originalName = null;

        if(APPEND_ORIGINAL_NAME) {
            MediaItem media = mediaService.queryByHash(hash);
            if (media != null) {

                MediaSource source = media.getSource();
                originalName = source.getResourceLocation().getName();
                try {
                    originalName = URLEncoder.encode(originalName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        UriBuilder streamLocationBuilder = UriBuilder.fromUri(getParent(uriInfo.getBaseUri()))
                .path("stream").path(hash);

        if (originalName != null)
            streamLocationBuilder.path(originalName);


        URI streamLocation = streamLocationBuilder.build();

        if (redirect) {
            // REDIRECT (default)
            return Response.seeOther(streamLocation).build();
        } else {
            // Just return a link
            return Response.ok(streamLocation.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
        }

    }
}
