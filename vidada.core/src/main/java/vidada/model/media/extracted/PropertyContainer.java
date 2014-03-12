package vidada.model.media.extracted;

import java.util.HashMap;
import java.util.Map;


class PropertyContainer {

	public Map<String, String> properties = new HashMap<String, String>();

	public String getProperty(String key){
		return properties.get(key);
	}

	public void setProperty(String key, String value){
		properties.put(key, value);
	}

}
