package vidada.client.services;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.events.EventArgs;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;

/**
 * Provides an API to access media items.
 * @author IsNull
 *
 */
public interface IMediaClientService {

    /**
     * Event which is fired when there are changes in the available medias.
     * (I.e when new medias are added or medias where removed)
     *
     * Warning: Not all vidada client connections support events.
     *
     * @return
     */
    public IEvent<EventArgs> getMediasChanged();


	/**
	 * Query for all medias which match the {@link MediaQuery}
	 * 
	 * @param qry
	 * @param pageIndex
	 * @param maxPageSize
	 * @return
	 */
	public abstract ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize);

	/**
	 * Returns the total count of all medias
	 */
	public abstract int count();

	/**
	 * Update all changes made to this media.
	 * @param media
	 */
	public abstract void update(MediaItem media);

	/**
	 * Gets the resource location for this media.
     * This is a path / URL which links directly to the raw media data.
     *
	 * @param media
	 * @return
	 */
	public abstract ResourceLocation openResource(MediaItem media);
}
