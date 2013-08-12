package vidada.model.media.source;

import vidada.model.entities.BaseEntity;

/**
 * Represents a media source, such as a local file, a stream etc.
 * 
 * @author IsNull
 *
 */
public abstract class MediaSource extends BaseEntity {

	/**
	 * Gets the original name of this media
	 * @return
	 */
	public abstract String getName();

	/**
	 * Is this media source available?
	 * @return
	 */
	public abstract boolean isAvailable();

	/**
	 * Open this media (with the OS default presenter)
	 */
	public abstract boolean open();

	/**
	 * 
	 * @return
	 */
	public abstract String retriveHash();

	/**
	 * Returns the path / url of this source if any.
	 * @return
	 */
	public abstract String getPath();
}
