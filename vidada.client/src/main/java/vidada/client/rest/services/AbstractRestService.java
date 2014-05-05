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
	 * Creates a new AbstractRestService
	 * @param client
	 * @param api base URI of the API
	 */
	protected AbstractRestService(Client client, URI api, ObjectMapper mapper){
		this.client = client;
		this.api = api;
		this.mapper = mapper;
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


    /**
     * Deserialize the given JSON String to an object
     * @param jsonData JSON data
     * @param type The Jackson Type-Reference (used for generics) to de-serialize to.
     * @param <T>
     * @return
     */
	public <T> T deserialize(String jsonData, TypeReference<T> type){
		T obj = null;
		try {
			obj = getMapper().readValue(jsonData, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

    /**
     * Deserialize the given JSON String to an object
     * @param jsonData JSON data
     * @param type The class type to de-serialize to.
     * @param <T>
     * @return
     */
	public <T> T deserialize(String jsonData, Class<T> type){
		T obj = null;
		try {
			obj = getMapper().readValue(jsonData, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}

    /**
     * Builds a multi value URL Query parameter.
     * Multiple values are usually delimited by a space (becoming a '+' in the URL)
     * @param values All values to turn into a multi value parameter
     * @return Returns the multi value parameter ready to insert in a query parameter.
     */
	protected static String buildMultiValueURLQueryParam(Collection<?> values){
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
