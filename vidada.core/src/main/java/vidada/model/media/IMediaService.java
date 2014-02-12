package vidada.model.media;

import java.util.Set;

import vidada.model.media.store.local.LocalMediaStore;
import archimedesJ.services.IService;

public interface IMediaService extends IService{


	// Events

	/**
	 * Raised when medias have changed in any way
	 * @return

	public IEvent<CollectionEventArg<MediaItem>> getMediasChangedEvent();
	 */
	/**
	 * Raised when the given Media Data has been changed
	 * @return

	public abstract IEvent<EventArgsG<MediaItem>> getMediaDataChangedEvent();
	 */

	// TODO: Add paging support in query
	// int page, int pagesize
	public Set<MediaItem> query(MediaQuery qry);

	/**
	 * Update the changed media in its origin media-store
	 * @param media
	 */
	public void update(MediaItem media);


	/**
	 * Gets the unique local media store
	 * @return
	 */
	public abstract LocalMediaStore getLocalMediaStore();
}