package vidada.server.rest.streaming;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.server.services.IMediaService;
import archimedesJ.io.locations.ResourceLocation;

public class MediaStreamHttpHandler extends SeekableHttpHandler {

	private final IMediaService mediaService;


	public MediaStreamHttpHandler(IMediaService mediaService){
		this.mediaService = mediaService;
	}

	@Override
	protected ResourceLocation getStreamResource(Request request, Response response, String relativeUri) {

		ResourceLocation resource = null;
		relativeUri = relativeUri.replaceAll("/", "");

		MediaItem media = mediaService.queryByHash(relativeUri);

		if(media != null){
			MediaSource localSource = media.getSource();
			if(localSource != null){
				resource = localSource.getResourceLocation();
			}else{
				System.err.println("Server: Stream - Media has no source!");
			}
		}else{
			System.out.println("Requested media '" + relativeUri + "' could not be found.");
		}


		return resource;
	}

	@Override
	protected boolean isStreamCompressionEnabled() {
		return false;
	}

}
