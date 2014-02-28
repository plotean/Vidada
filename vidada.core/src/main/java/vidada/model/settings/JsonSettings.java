package vidada.model.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Base class for Json based settings
 * @author IsNull
 *
 */
public class JsonSettings {

	private transient File settingsPath = null;
	private String settingsVersion = "1.0";

	protected JsonSettings() { }


	public String getSettingsVersion(){
		return settingsVersion;
	}

	protected void setSettingsVersion(String version){
		settingsVersion = version;
	}

	protected void setPath(File path){
		this.settingsPath = path;
	}

	public void persist() {

		assert settingsPath != null : "You must set the Settings path before calling persist()!";

		ObjectMapper mapper = buildMapper();

		try {
			mapper.writeValue(settingsPath, this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <T> T loadSettings(File path, Class<T> type){
		T settings = null;
		try {
			ObjectMapper mapper = buildMapper();
			settings = mapper.readValue(path, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return settings;
	}

	private static ObjectMapper buildMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		mapper.setVisibility(JsonMethod.ALL, Visibility.NONE);
		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		return mapper;
	}


}
