package vidada.client.rest.services;

import java.io.IOException;
import java.net.URI;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Base class for all Rest-Client Services
 * @author IsNull
 *
 */
public abstract class AbstractRestService {

	private final ObjectMapper mapper;
	private final Client client;
	private final URI api;


	/**
	 * 
	 * @param client
	 * @param api base URI of the API
	 */
	protected AbstractRestService(Client client, URI api, ObjectMapper mapper){
		this.client = client;
		this.api = api;
		this.mapper = mapper;
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

	protected ObjectMapper getMapper(){
		return mapper;
	}


	public <T> T deserialize(String data, TypeReference<T> type){
		T obj = null;
		try {
			obj = getMapper().readValue(data, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public <T> T deserialize(String data, Class<T> type){
		T obj = null;
		try {
			obj = getMapper().readValue(data, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
