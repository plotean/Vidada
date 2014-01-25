package vidada.model.media;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.local.LocalMediaStore;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.util.Lists;

/**
 * Central service which manages all media items.
 * The media service manages all media-stores which then manage the details of the medias.
 * 
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class MediaService implements IMediaService {

	transient private final LocalMediaStore localMediaStore = new LocalMediaStore();
	transient private final List<IMediaStore> allStores = new ArrayList<IMediaStore>();



	// Events

	private final MediaFileInfo[] supportedMedias = MediaFileInfo.getKnownMediaInfos();
	private EventHandlerEx<CollectionEventArg<MediaItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<MediaItem>>();
	@Override
	public IEvent<CollectionEventArg<MediaItem>> getMediasChangedEvent() { return mediasChangedEvent; }
	private EventHandlerEx<EventArgsG<MediaItem>> mediaDataChangedEvent = new EventHandlerEx<EventArgsG<MediaItem>>();
	@Override
	public IEvent<EventArgsG<MediaItem>> getMediaDataChangedEvent() { return mediaDataChangedEvent; }


	public MediaService(){

		allStores.add(localMediaStore);

		String[] allMediaExtensions = new String[0];
		for (MediaFileInfo media : supportedMedias) {
			allMediaExtensions = Lists.concat(allMediaExtensions, media.getFileExtensions());
		}
	}

	@Override
	public LocalMediaStore getLocalMediaStore(){
		return localMediaStore;
	}

	public Collection<IMediaStore> getStores(){
		return allStores;
	}

	@Override
	public Set<MediaItem> query(MediaQuery qry) {

		HashSet<MediaItem> resultSet = new HashSet<MediaItem>(2000);

		for (IMediaStore store : getStores()) {
			resultSet.addAll(store.query(qry));
		}

		return resultSet;
	}













	@Override
	public void addMediaData(MediaItem mediadata) {
		addMediaData(Lists.asList(mediadata));
	}


	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#addMediaData(java.util.List)
	 */
	@Override
	public void addMediaData(Iterable<MediaItem> mediadatas){

		// TODO


		onMediaDataAdded(Lists.toList(mediadatas));
	}


	@Override
	public void removeMediaData(MediaItem mediadata) {
		removeMediaData(Lists.asList(mediadata));
	}

	/* (non-Javadoc)
	 * @see vidada.model.media.IMediaService2#removeMediaData(java.util.List)
	 */
	@Override
	public void removeMediaData(Iterable<MediaItem> mediadatas){

		// TODO

		onMediaDataRemoved(Lists.toList(mediadatas));
	}


	protected void onMediaDataAdded(List<MediaItem> mediadatas){
		mediasChangedEvent.fireEvent(this, new CollectionEventArg<MediaItem>(CollectionChangeType.Added, mediadatas));
	}

	protected void onMediaDataRemoved(List<MediaItem> mediadatas){
		mediasChangedEvent.fireEvent(this, new CollectionEventArg<MediaItem>(CollectionChangeType.Removed, mediadatas));
	}



	@Override
	public void update(MediaItem mediadata) {
		// TODO
	}

	@Override
	public void update(Iterable<MediaItem> mediadatas) {
		// TODO
	}


	@Override
	public void removeAll() {
		// TODO

		mediasChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
	}





}
