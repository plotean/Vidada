package vidada.client.rest.services;

import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import vidada.client.services.IThumbnailClientService;
import vidada.model.media.MediaItem;
import vidada.services.ServiceProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;

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
				.request("image/png").get(InputStream.class);
		return rawImageFactory.createImage(istream);
	}

	@Override
	public boolean renewThumbImage(MediaItem media, float pos) {
		Response r =  thumbResource()
				.path(media.getFilehash())
				.request()
				.post( Entity.entity(pos, MediaType.TEXT_PLAIN_TYPE) );
		return r.getStatus() == 202;
	}


	protected WebTarget thumbResource(){
		return apiResource().path("thumbnail");
	}

}
