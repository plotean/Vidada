package vidada.client.rest;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import vidada.services.IMediaService;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.io.locations.ResourceLocation;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class MediaServiceRestClient extends RESTClientService implements IMediaService {


	public MediaServiceRestClient(Client client, URI api) {
		super(client, api);
	}





	@Override
	public void update(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize) {
		throw new NotImplementedException();
	}

	@Override
	public int count() {
		String countStr = mediasResource().path("count").accept(MediaType.TEXT_PLAIN).get(String.class);
		return Integer.parseInt(countStr);
	}


	// Not implemented in remote service

	@Override
	public void store(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public void store(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}


	@Override
	public void update(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}

	@Override
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		throw new NotImplementedException();
	}

	@Override
	public List<MediaItem> getAllMedias() {
		throw new NotImplementedException();
	}

	@Override
	public void delete(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}

	@Override
	public MediaItem queryByHash(String hash) {
		throw new NotImplementedException();
	}
	//

	protected WebResource mediasResource(){
		return apiResource().path("medias");
	}
}
