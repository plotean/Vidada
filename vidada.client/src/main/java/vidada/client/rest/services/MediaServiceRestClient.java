package vidada.client.rest.services;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class MediaServiceRestClient extends AbstractRestService implements IMediaClientService {

	public MediaServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public void update(MediaItem media) {
		try {
			String mediaJson = getMapper().writeValueAsString(media);
			mediasResource()
			.type(MediaType.APPLICATION_JSON_TYPE)
			.accept(MediaType.APPLICATION_JSON_TYPE)
			.post(mediaJson);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize) {

		/*String mediasJson = mediasResource()

				.queryParam("tags", multiValueQueryParam(qry.getRequiredTags()))
				.queryParam("type", qry.getMediaType().toString())
				.queryParam("orderby", qry.getOrder().toString())
				.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);*/

		WebResource resource = mediasResource()
				.queryParam("page", pageIndex+"")
				.queryParam("pageSize", maxPageSize+"");

		if(qry.getKeywords() != null && !qry.getKeywords().isEmpty()){
			resource.queryParam("query", qry.getKeywords());
		}

		if(!qry.getRequiredTags().isEmpty()){
			resource.queryParam("tags", multiValueQueryParam(qry.getRequiredTags()));
		}

		if(qry.getMediaType() != null){
			resource.queryParam("type", qry.getMediaType().toString());
		}


		String mediasJson =	resource.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);

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
