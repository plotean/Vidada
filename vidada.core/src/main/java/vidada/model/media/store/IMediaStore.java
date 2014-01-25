package vidada.model.media.store;

import java.util.Collection;
import java.util.Set;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;

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

	public void update(MediaItem media);

	public void update(Collection<MediaItem> media);

	public void delete(MediaItem media);

	public void delete(Collection<MediaItem> media);


	// TODO: Add paging support in query
	// int page, int pagesize
	public Set<MediaItem> query(MediaQuery qry);

	/**
	 * Re-import the whole medias - this will take some time
	 */
	public void synchronize(); // TODO return async task object or something like that

}
