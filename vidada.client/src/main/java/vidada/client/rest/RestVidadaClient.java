package vidada.client.rest;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import vidada.client.IVidadaClient;
import vidada.client.rest.services.MediaServiceRestClient;
import vidada.client.rest.services.TagServiceRestClient;
import vidada.client.rest.services.ThumbnailRestClientService;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RestVidadaClient implements IVidadaClient{

	private final URI vidadaBaseUri;
	private final Client client;


	private final IMediaClientService mediaClientService;
	private final ITagClientService tagClientService;
	private final IThumbnailClientService thumbnailClientService;

	/**
	 * Rest client
	 */
	public RestVidadaClient(URI vidadaBaseUri){

		this.vidadaBaseUri = vidadaBaseUri;
		ClientConfig config = new DefaultClientConfig();

		client = Client.create(config);
		// TODO replace with real credentials
		client.addFilter(new HTTPBasicAuthFilter("admin", "1337")); 

		mediaClientService = new MediaServiceRestClient(client, vidadaBaseUri, getJsonMapper());
		tagClientService = new TagServiceRestClient(client, vidadaBaseUri, getJsonMapper());
		thumbnailClientService = new ThumbnailRestClientService(client, vidadaBaseUri, getJsonMapper());
	}


	@Override
	public IMediaClientService getMediaClientService() {
		return mediaClientService;
	}

	@Override
	public ITagClientService getTagClientService() {
		return tagClientService;
	}

	@Override
	public IThumbnailClientService getThumbnailClientService() {
		return thumbnailClientService;
	}

	@Override
	public String getInstanceId() {
		return vidadaBaseUri.getHost();
	}

	private ObjectMapper mapper;

	private ObjectMapper getJsonMapper(){
		if(mapper == null){
			mapper = new ObjectMapper();
			mapper.setVisibility(JsonMethod.ALL, Visibility.NONE);
			mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		}
		return mapper;
	}
}
