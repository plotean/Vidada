package vidada.client.rest;

import java.net.URI;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public abstract class RESTClientService {

	private final Client client;
	private final URI api;


	protected RESTClientService(Client client, URI api){
		this.client = client;
		this.api = api;
	}


	public void test() {
		// Get plain text
		//System.out.println(resource().path("rest").path("hello").accept(MediaType.TEXT_PLAIN).get(String.class));
	}

	protected WebResource apiResource(){
		return client.resource(getApiURI());
	}

	private URI getApiURI() {
		return api;
	}

}
