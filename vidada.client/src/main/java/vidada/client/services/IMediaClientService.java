package vidada.client.services;

import java.util.List;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;

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
	 * @param qry
	 * @return
	 */
	public abstract List<MediaItem> query(MediaQuery qry);

	/**
	 * Update all changes made to this media.
	 * @param media
	 */
	public abstract void update(MediaItem media);
}
