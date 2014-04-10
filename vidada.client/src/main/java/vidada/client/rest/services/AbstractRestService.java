package vidada.client.rest.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;


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

	protected WebTarget apiResource(){
		return client.target(getApiURI());
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

	protected String multiValueQueryParam(Collection<?> values){
		StringBuilder builder = new StringBuilder();
		if(!values.isEmpty()){
			for (Object object : values) {
				builder.append(object.toString());
				builder.append(" ");
			}
			return builder.substring(0, builder.length()-1);
		}
		return "";
	}
}
