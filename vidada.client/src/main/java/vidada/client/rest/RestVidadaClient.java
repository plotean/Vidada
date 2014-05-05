package vidada.client.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import vidada.client.IVidadaClient;
import vidada.client.rest.services.MediaServiceRestClient;
import vidada.client.rest.services.TagServiceRestClient;
import vidada.client.rest.services.ThumbnailRestClientService;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;


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

		ClientConfig config = new ClientConfig();
		client = ClientBuilder.newClient(config);

		// TODO replace with real credentials
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
				.nonPreemptive().credentials("admin", "1337").build();
		client.register(feature); 

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
			mapper.registerModule(new JodaModule());
			mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		}
		return mapper;
	}
}
