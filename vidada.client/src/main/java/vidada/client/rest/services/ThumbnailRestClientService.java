package vidada.client.rest.services;

import java.net.URI;

import org.codehaus.jackson.map.ObjectMapper;

import vidada.client.services.IThumbnailClientService;
import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

import com.sun.jersey.api.client.Client;

public class ThumbnailRestClientService extends AbstractRestService implements IThumbnailClientService {

	public ThumbnailRestClientService(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public IMemoryImage retrieveThumbnail(MediaItem media, Size size) {
		System.err.println("ThumbnailRestClientService: retrieveThumbnail() Not implemented!");
		return null;
	}

}
