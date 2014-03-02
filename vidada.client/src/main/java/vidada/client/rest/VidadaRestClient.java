package vidada.client.rest;

import java.net.URI;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class VidadaRestClient{

	private URI vidadaBaseUri;
	private Client client;

	public VidadaRestClient(URI vidadaBaseUri){

	}

	public void init(){
		ClientConfig config = new DefaultClientConfig();
		client = Client.create(config);


	}

}
