package vidada.model.media;

import java.util.ArrayList;
import java.util.List;

import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;



public class MediaBrowserModel {

	private final List<MediaItem> medias = new ArrayList<MediaItem>();

	private final EventHandlerEx<CollectionEventArg<MediaItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<MediaItem>>();
	/**
	 * Raised when the media model has changed
	 */
	public IEvent<CollectionEventArg<MediaItem>> getMediasChangedEvent() {return mediasChangedEvent;}


	public MediaBrowserModel(){

	}

	public void clear() {
		medias.clear();
		mediasChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
	}

	public void addAll(List<MediaItem> newMedias) {
		medias.addAll(newMedias);
		mediasChangedEvent.fireEvent(this, CollectionEventArg.Invalidated);
	}


	public MediaItem getItem(int index){
		return medias.get(index);
	}

	/**
	 * Gets a copy of the internal media list
	 * @return
	 */
	public List<MediaItem> getSnapshot(){
		return new ArrayList<MediaItem>(medias);
	}


	public int size() {
		return medias.size();
	}


	public boolean isEmpty() {
		return medias.isEmpty();
	}

	/**
	 * Returns the internal list reference
	 * @return
	 */
	public List<MediaItem> getRaw() {
		return medias;
	}



}