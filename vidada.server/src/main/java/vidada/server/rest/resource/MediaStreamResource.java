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

/**
 * Represents a media stream of a single media resource
 * path: medias/{hash}/stream
 */
public class MediaStreamResource extends AbstractResource {

    private static final boolean APPEND_ORIGINAL_NAME = true;

    private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();

    private final String mediaHash;

    public MediaStreamResource(String hash){
        this.mediaHash = hash;
    }

    @GET
    public Response getMedia(
            @Context UriInfo uriInfo,
            @QueryParam("redirect") @DefaultValue("true") boolean redirect) {

        MediaItem media = mediaService.queryByHash(mediaHash);
        if (media != null) {

            //
            // Streams are handled by a special module outside the REST API
            //

            UriBuilder streamLocationBuilder = UriBuilder.fromUri(getParent(uriInfo.getBaseUri()))
                    .path("stream").path(mediaHash);

            if (APPEND_ORIGINAL_NAME)
                streamLocationBuilder.path(getMediaURLName(media));


            URI streamLocation = streamLocationBuilder.build();

            if (redirect) {
                // REDIRECT (default)
                return Response.seeOther(streamLocation).build();
            } else {
                // Just return a link
                return Response.ok(streamLocation.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
            }
        }else{
            // Unknown media hash
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    private String getMediaURLName(MediaItem media){
        String name;
        MediaSource source = media.getSource();
        name = source.getResourceLocation().getName();
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }
}
