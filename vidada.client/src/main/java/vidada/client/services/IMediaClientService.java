package vidada.client.services;

import java.util.List;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;

public interface IMediaClientService {

	/**
	 * Query all Vidada servers for matching medias
	 * @param qry
	 * @return
	 */
	public abstract List<MediaItem> query(MediaQuery qry);

	/**
	 * Update all changes made to this media
	 * @param media
	 */
	public abstract void update(MediaItem media);
}
