package vidada.client.rest;

import java.net.URI;

import vidada.client.IVidadaClient;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestVidadaClient implements IVidadaClient{

	private URI vidadaBaseUri;
	private Client client;

	public RestVidadaClient(URI vidadaBaseUri){

	}

	public void init(){
		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);

	}

	@Override
	public IMediaClientService getMediaClientService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITagClientService getTagClientService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IThumbnailClientService getThumbnailClientService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstanceId() {
		// TODO Auto-generated method stub
		return null;
	}

}
