package vidada.client.rest;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import archimedesJ.exceptions.NotImplementedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class MediaServiceRestClient extends RESTClientService implements IMediaClientService {

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


	protected WebResource mediasResource(){
		return apiResource().path("medias");
	}
}
