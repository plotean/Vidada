package vidada.client.rest.services;

import archimedes.core.events.EventArgs;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class MediaServiceRestClient extends AbstractRestService implements IMediaClientService {


    @Override
    public IEvent<EventArgs> getMediasChanged() {
        return null; // TODO
    }


	public MediaServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public void update(MediaItem media) {
		try {
			String mediaJson = getMapper().writeValueAsString(media);
			mediasResource()
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post( Entity.entity(mediaJson, MediaType.APPLICATION_JSON_TYPE));
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

		WebTarget resource = mediasResource()
				.queryParam("page", pageIndex+"")
				.queryParam("pageSize", maxPageSize+"")
				.queryParam("orderBy", qry.getOrder().name())
                .queryParam("reverse", qry.isReverseOrder());

		if(qry.getKeywords() != null && !qry.getKeywords().isEmpty()){
			resource = resource.queryParam("query", qry.getKeywords());
		}

		if(!qry.getRequiredTags().isEmpty()){
			resource = resource.queryParam("tags", multiValueQueryParam(qry.getRequiredTags()));
		}

        if(!qry.getBlockedTags().isEmpty()){
            resource = resource.queryParam("tagsNot", multiValueQueryParam(qry.getBlockedTags()));
        }

		if(qry.getMediaType() != null){
			resource = resource.queryParam("type", qry.getMediaType().name());
		}

		String mediasJson =	resource.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);

		ListPage<MediaItem> page = deserialize(mediasJson, new TypeReference<ListPage<MediaItem>>() {});
		return page;
	}

	@Override
	public int count() {
		String countStr = mediasResource().path("count").request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
		return Integer.parseInt(countStr);
	}

	@Override
	public ResourceLocation openResource(MediaItem media) {
		ResourceLocation mediaResource = null;

		// Request that a media stream server is started for this media

		String mediaStreamUri =  apiResource()
                .path("stream").path(media.getFilehash())
				.queryParam("redirect", false)
                .request(MediaType.TEXT_PLAIN_TYPE).get(String.class);

		try {
			mediaResource = ResourceLocation.Factory.create(mediaStreamUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return mediaResource;
	}



	protected WebTarget mediasResource(){
		return apiResource().path("medias");
	}
}
