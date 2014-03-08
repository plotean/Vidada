package vidada.client.rest.services;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import archimedesJ.exceptions.NotImplementedException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class MediaServiceRestClient extends AbstractRestService implements IMediaClientService {

	public MediaServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public void update(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize) {

		String mediasJson = mediasResource()
				.queryParam("query", qry.getKeywords())
				.queryParam("tags", multiValueQueryParam(qry.getRequiredTags()))
				.queryParam("type", qry.getMediaType().toString())
				.queryParam("orderby", qry.getOrder().toString())
				.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		ListPage<MediaItem> page = deserialize(mediasJson, new TypeReference<ListPage<MediaItem>>() {});
		return page;
	}

	@Override
	public int count() {
		String countStr = mediasResource().path("count").accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
		return Integer.parseInt(countStr);
	}

	protected WebResource mediasResource(){
		return apiResource().path("medias");
	}
}
