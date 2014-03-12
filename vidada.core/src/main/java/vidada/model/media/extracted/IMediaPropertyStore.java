package vidada.model.media.extracted;

import vidada.model.media.MediaItem;

public interface IMediaPropertyStore {

	/**
	 * Gets the property from the store
	 * @param media
	 * @param key
	 * @return The value or null if no property with the given key exists
	 */
	String getProperty(MediaItem media, String key);

	/**
	 * Sets the property and persists it
	 * @param media
	 * @param key
	 * @param value
	 * @return
	 */
	void setProperty(MediaItem media, String key, String value);
}
