package vidada.client.rest.services;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import vidada.client.services.ITagClientService;
import vidada.model.tags.Tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TagServiceRestClient extends AbstractRestService implements ITagClientService{

	public TagServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public Collection<Tag> getUsedTags() {
		String jsonTags =	tagsResource().path("used").accept(MediaType.APPLICATION_JSON).get(String.class);
		Collection<Tag> tags = deserialize(jsonTags, new TypeReference<Collection<Tag>>() { });
		return tags;
	}

	protected WebResource tagsResource(){
		return apiResource().path("tags");
	}
}
