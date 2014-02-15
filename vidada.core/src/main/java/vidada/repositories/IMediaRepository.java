package vidada.repositories;

import java.util.Collection;
import java.util.List;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;

public interface IMediaRepository {

	/**
	 * Query for all medias which match the given media-query
	 * @param qry
	 * @param dontOrder Skip ordering
	 * @return
	 */
	public abstract List<MediaItem> query(MediaQuery qry);

	/**
	 * Returns all media items which are in the given libraries
	 * @param libraries
	 * @return
	 */
	public abstract List<MediaItem> query(Collection<MediaLibrary> libraries);

	/**
	 * Query for all medias which have the given Tag
	 */
	public abstract Collection<MediaItem> query(Tag tag);

	public abstract void store(MediaItem mediadata);

	public abstract void store(Iterable<MediaItem> mediadatas);

	public abstract void delete(MediaItem mediadata);

	public abstract void delete(Iterable<MediaItem> mediadatas);

	public abstract List<MediaItem> getAllMediaData();

	public abstract void update(MediaItem mediadata);

	public abstract void update(Iterable<MediaItem> mediadatas);

	public abstract void removeAll();

	/**
	 * 
	 * @param hash
	 * @return
	 */
	public abstract MediaItem findMediaDataByHash(String hash);

}