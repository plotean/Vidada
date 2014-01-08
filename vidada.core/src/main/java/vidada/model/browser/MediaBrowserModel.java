package vidada.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.tags.TagStatesModel;
import vidada.viewmodel.tags.TagViewModel;
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
	private final ITagStatesVM tagStatesModel = new TagStatesModel(TagViewModel.VMFactory);


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

	public ITagStatesVM getTagStatesModel(){
		return tagStatesModel;
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

	/**
	 * Gets a copy of the internal media list
	 * @return
	 */
	public List<IBrowserItem> getSnapshot(){
		return new ArrayList<IBrowserItem>(medias);
	}


	@Override
	public int size() {
		return medias.size();
	}


	@Override
	public boolean isEmpty() {
		return medias.isEmpty();
	}

	/**
	 * Returns the internal list reference
	 * @return
	 */
	public List<IBrowserItem> getRaw() {
		return medias;
	}

}
