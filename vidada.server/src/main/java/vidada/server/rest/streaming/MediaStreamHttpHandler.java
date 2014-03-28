package vidada.server.rest.streaming;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.server.services.IMediaService;
import archimedes.core.io.locations.ResourceLocation;

public class MediaStreamHttpHandler extends SeekableHttpHandler {

	private final IMediaService mediaService;


	public MediaStreamHttpHandler(IMediaService mediaService){
		this.mediaService = mediaService;
	}

	@Override
	protected ResourceLocation getStreamResource(Request request, Response response, String relativeUri) {

		ResourceLocation resource = null;
		String[] parts = relativeUri.split("/");
        if(parts.length > 1) {
            String hash = parts[1];
            MediaItem media = mediaService.queryByHash(hash);

            if (media != null) {
                MediaSource localSource = media.getSource();
                if (localSource != null) {
                    resource = localSource.getResourceLocation();
                } else {
                    System.err.println("Server: Stream - Media has no source!");
                }
            } else {
                System.out.println("Requested media '" + relativeUri + "' could not be found. Hash = " + hash);
            }
        }
		return resource;
	}

	@Override
	protected boolean isStreamCompressionEnabled() {
		return false;
	}

}
