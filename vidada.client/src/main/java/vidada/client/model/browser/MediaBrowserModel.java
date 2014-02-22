package vidada.client.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.model.pagination.IDataProvider;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;


/**
 * MediaBrowserModel for the MediaBrowserFX
 * @author IsNull
 *
 */
public class MediaBrowserModel implements IDataProvider<IBrowserItem> {

	private final List<IBrowserItem> medias = new ArrayList<IBrowserItem>();


	//
	// Events
	//
	private final EventHandlerEx<CollectionEventArg<IBrowserItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<IBrowserItem>>();
	/**
	 * Raised when the media model has changed
	 */
	@Override
	public IEvent<CollectionEventArg<IBrowserItem>> getItemsChangedEvent() {return mediasChangedEvent;}


	public MediaBrowserModel(){

	}

	public void clear() {
		medias.clear();
		mediasChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
	}

	public void addAll(List<IBrowserItem> newMedias) {
		medias.addAll(newMedias);
		mediasChangedEvent.fireEvent(this, CollectionEventArg.Invalidated);
	}

	/**
	 * Gets the item at the specific index
	 */
	@Override
	public IBrowserItem get(int index){
		return medias.size() > index ? medias.get(index) : null;
	}

	@Override
	public int size() {
		return medias.size();
	}


	@Override
	public boolean isEmpty() {
		return medias.isEmpty();
	}

}
