package vidada.server.rest.resource;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javax.ws.rs.Path;
import java.io.IOException;
import java.net.URI;


/**
 * Base class for all resources
 */
public abstract class AbstractResource {


	private static ObjectMapper mapper;


    @Path("metadata")
    public MetadataResource getMetadata(){
        return new MetadataResource(this.getClass());
    }


	private ObjectMapper getMapper(){
		if(mapper == null){
			mapper = buildMapper();
		}
		return mapper;
	}

	private static ObjectMapper buildMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper;
	}

	/**
	 * Serializes the given Object to JSON
	 * @param o
	 * @return
	 */
	protected final String serializeJson(Object o){
		try {
			return getMapper().writeValueAsString(o);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "403 - Internal Error";
	}

    protected final <T> T deserialize(String data, TypeReference<T> type){
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

	protected final <T> T deserialize(String data, Class<T> type){
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

	protected final URI getParent(URI uri){
		return  uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
	}

	protected final String[] parseMultiValueParam(String multiParams){
		return multiParams != null ? multiParams.split(" ") : null;
	}
}
