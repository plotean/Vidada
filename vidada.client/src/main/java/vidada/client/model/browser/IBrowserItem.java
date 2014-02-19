package vidada.client.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;

/**
 * Represents a single item in the media Browser. This can be an item or a folder.
 * @author IsNull
 *
 */
public interface IBrowserItem {

	/**
	 * The name of the item
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns all children of this item.
	 * @return
	 */
	public abstract List<IBrowserItem> getChildren();

	/**
	 * Is this a folder?
	 * @return
	 */
	public abstract boolean isFolder();

	/**
	 * Get the media item for this file.
	 * @return
	 */
	public abstract MediaItem getData();
}
