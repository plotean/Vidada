package vidada.client.rest.services;

import java.io.InputStream;
import java.net.URI;

import org.codehaus.jackson.map.ObjectMapper;

import vidada.client.services.IThumbnailClientService;
import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.images.IRawImageFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ThumbnailRestClientService extends AbstractRestService implements IThumbnailClientService {

	private IRawImageFactory rawImageFactory = ServiceProvider.Resolve(IRawImageFactory.class);


	public ThumbnailRestClientService(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public IMemoryImage retrieveThumbnail(MediaItem media, Size size) {
		InputStream istream = thumbResource()
				.path(media.getFilehash())
				.queryParam("width", size.width+"")
				.queryParam("height", size.height+"")
				.accept("image/png").get(InputStream.class);
		return rawImageFactory.createImage(istream);
	}


	protected WebResource thumbResource(){
		return apiResource().path("thumbnail");
	}

}
