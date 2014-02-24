package vidada.client.services;

import vidada.IVidadaServer;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;

/**
 * Provides an API to control media items.
 * @author IsNull
 *
 */
public interface IMediaClientService {

	/**
	 * Query for all medias which match the {@link MediaQuery}
	 * 
	 * Internally queries all known Vidada servers and then merges all the results.
	 * 
	 * @param qry
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public abstract ListPage<MediaItem> queryServer(IVidadaServer server,MediaQuery qry, int pageIndex, int maxPageSize);

	/**
	 * Update all changes made to this media.
	 * @param media
	 */
	public abstract void update(MediaItem media);
}
