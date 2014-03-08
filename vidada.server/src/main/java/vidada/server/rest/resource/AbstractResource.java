package vidada.server.rest.resource;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

public abstract class AbstractResource {


	private static ObjectMapper mapper;

	private ObjectMapper getMapper(){
		if(mapper == null){
			mapper = buildMapper();
		}
		return mapper;
	}

	private static ObjectMapper buildMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		mapper.setVisibility(JsonMethod.ALL, Visibility.NONE);
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		return mapper;
	}

	/**
	 * Serializes the given Object to JSON
	 * @param o
	 * @return
	 */
	protected String serializeJson(Object o){
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

	protected String[] parseMultiValueParam(String multiParams){
		return multiParams != null ? multiParams.split(" ") : null;
	}
}
