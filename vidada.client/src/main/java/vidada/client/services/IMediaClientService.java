package vidada.client.services;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import archimedes.core.io.locations.ResourceLocation;

/**
 * Provides an API to control media items.
 * @author IsNull
 *
 */
public interface IMediaClientService {

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
	 * Gets the resource location for this media
	 * @param mediaData
	 * @return
	 */
	public abstract ResourceLocation openResource(MediaItem media);
}
