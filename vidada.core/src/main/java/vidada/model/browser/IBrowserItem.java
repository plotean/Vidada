package vidada.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;

/**
 * Represents a single item in the media Browser, which can be an item or a folder
 * @author IsNull
 *
 */
public interface IBrowserItem {

	public abstract String getName();

	public abstract List<IBrowserItem> getChildren();

	public abstract boolean isFolder();


	public abstract MediaItem getData();
}
