package vidada.model.media.store;

import java.util.Collection;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.movies.MovieMediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * A media store is a database of medias, that means it defines
 * an abstract pool of media items. A media store can be local
 * or a remote sever. 
 * 
 * @author IsNull
 *
 */
public interface IMediaStore {

	/**
	 * Returns the media store name id
	 */
	public String getNameId();

	/**
	 * Update the media details such as
	 * - Name
	 * - Rating
	 * 
	 * @param media
	 */
	public void update(MediaItem media);
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
	 * Get the current thumbnail for the given media
	 * @param media
	 * @param size
	 * @return
	 */
	public IMemoryImage GetThumbImage(MediaItem media, Size size);

	/**
	 * Renews the thumbnail of the given media from position pos and returns the new thumbnail.
	 * 
	 * @param media
	 * @param size
	 * @param pos
	 * @return
	 */
	public IMemoryImage RenewThumbImage(MovieMediaItem media, Size size, float pos);



	/**
	 * Re-import the whole medias - this will take some time
	 */
	public void synchronize(); // TODO return async task object or something like that
}
