package vidada.model.media;

import java.util.Set;

import vidada.model.media.store.local.LocalMediaStore;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;
import archimedesJ.services.IService;

public interface IMediaService extends IService{


	// Events

	/**
	 * Raised when medias have changed in any way
	 * @return
	 */
	public IEvent<CollectionEventArg<MediaItem>> getMediasChangedEvent();

	/**
	 * Raised when the given Media Data has been changed
	 * @return
	 */
	public abstract IEvent<EventArgsG<MediaItem>> getMediaDataChangedEvent();


	// TODO: Add paging support in query
	// int page, int pagesize
	public Set<MediaItem> query(MediaQuery qry);


	/**
	 * Add the given mediadata to this service and persist it.
	 * @param mediadata
	 */
	public abstract void addMediaData(MediaItem mediadata);

	/**
	 * Removes the given mediadata from this service
	 * @param mediadata
	 */
	public abstract void removeMediaData(MediaItem mediadata);



	/**
	 * Add the given mediadatas to this service and persist it.
	 * @param mediadata
	 */
	public abstract void addMediaData(Iterable<MediaItem> mediadata);

	/**
	 * Removes the given mediadata from this service
	 * @param mediadata
	 */
	public abstract void removeMediaData(Iterable<MediaItem> mediadata);

	/**
	 * Remove all medias
	 */
	public abstract void removeAll();

	/**
	 * Persists the given mediadata
	 * @param mediadata
	 */
	public abstract void update(MediaItem mediadata);


	/**
	 * Persists the given media datas
	 * @param mediadata
	 */
	public abstract void update(Iterable<MediaItem> mediadata);

	/**
	 * Gets the unique local media store
	 * @return
	 */
	public abstract LocalMediaStore getLocalMediaStore();


}