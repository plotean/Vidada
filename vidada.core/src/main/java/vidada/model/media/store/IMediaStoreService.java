package vidada.model.media.store;

import java.util.Collection;

import vidada.model.media.MediaItem;
import vidada.model.media.store.local.LocalMediaStore;
import archimedesJ.services.IService;

/**
 * Manages all media stores
 * @author IsNull
 *
 */
public interface IMediaStoreService extends IService{

	/**
	 * Get the local media store
	 * @return
	 */
	public LocalMediaStore getLocalMediaStore();

	/**
	 * Get all media stores
	 * @return
	 */
	public Collection<IMediaStore> getStores();

	/**
	 * Get the media store where this item is in.
	 * @param media
	 * @return
	 */
	public IMediaStore getStore(MediaItem media);
}
