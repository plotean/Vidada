package vidada.client.rest.services;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import vidada.client.services.ITagClientService;
import vidada.model.tags.Tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TagServiceRestClient extends AbstractRestService implements ITagClientService{

	public TagServiceRestClient(Client client, URI api, ObjectMapper mapper) {
		super(client, api, mapper);
	}

	@Override
	public Collection<Tag> getUsedTags() {
		String jsonTags =	tagsResource().path("used").request(MediaType.APPLICATION_JSON).get(String.class);
		Collection<Tag> tags = deserialize(jsonTags, new TypeReference<Collection<Tag>>() { });
		return tags;
	}

	protected WebTarget tagsResource(){
		return apiResource().path("tags");
	}
}
