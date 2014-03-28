package vidada.model.media.extracted;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import archimedesJ.exceptions.NotImplementedException;
import vidada.model.media.MediaItem;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MediaPropertyStore implements IMediaPropertyStore {

	private final Map<String, PropertyContainer> conatinerCache = new HashMap<String, PropertyContainer>();
	private final File propertyStoreFolder;

	public MediaPropertyStore(File propertyStoreFolder){
		this.propertyStoreFolder = propertyStoreFolder;
	}

	@Override
	public String getProperty(MediaItem media, String key) {
		return getContainer(media).getProperty(key);
	}

	@Override
	public void setProperty(MediaItem media, String key, String value) {
		PropertyContainer container = getContainer(media);
		container.setProperty(key, value);
		persistContainer(container);
	}



	private File getContainerPath(MediaItem media){
		return new File(propertyStoreFolder, media.getFilehash() + ".json");
	}

	private void persistContainer(PropertyContainer container){
		throw new NotImplementedException();
	}

	private PropertyContainer getContainer(MediaItem media){
		PropertyContainer container = conatinerCache.get(media.getFilehash());

		if(container == null){
			// try to read it from serialized file
			File containerPath = getContainerPath(media);
			if(containerPath.exists()){
				try {
					container = getMapper().readValue(containerPath, PropertyContainer.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// No existing properties could be found
			if(container == null){
				container = new PropertyContainer();
			}

			// Cache the container
			conatinerCache.put(media.getFilehash(), container);
		}
		return container;
	}

	private transient static ObjectMapper mapper;

	private synchronized static ObjectMapper getMapper(){
		if(mapper == null){
			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		}
		return mapper;
	}
}
