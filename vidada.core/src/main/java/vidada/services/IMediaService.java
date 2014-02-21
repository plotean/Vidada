package vidada.services;

import java.util.Collection;
import java.util.List;

import vidada.IVidadaServer;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Manages all medias on a {@link IVidadaServer}
 * @author IsNull
 *
 */
public interface IMediaService {

	/**
	 * Store the new media item
	 * @param media
	 */
	public void store(MediaItem media);

	/**
	 * Store the new medias
	 * @param media
	 */
	public void store(Collection<MediaItem> media);

	/**
	 * Update the media details such as
	 * - Name
	 * - Rating
	 * 
	 * @param media
	 */
	public void update(MediaItem media);

	/**
	 * 
	 * @param media
	 */
	public void update(Collection<MediaItem> media);

	/**
	 * Query this media store for matching medias
	 * TODO: Add paging support in query (int page, int pagesize)
	 * 
	 * @param qry
	 * @return
	 */
	public Collection<MediaItem> query(MediaQuery qry);

	/**
	 * Finds an existing media item or creates a new one for the given file
	 * @param file
	 * @param persist
	 * @return
	 */
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist);

	/**
	 * Gets all media items
	 * @return
	 */
	public List<MediaItem> getAllMedias();

	/**
	 * Deletes all medias
	 * @param media
	 */
	public void delete(MediaItem media);

	/**
	 * Deletes all medias
	 * @param media
	 */
	public void delete(Collection<MediaItem> media);

}