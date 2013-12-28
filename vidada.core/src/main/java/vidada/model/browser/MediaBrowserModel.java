package vidada.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.tags.TagStatesModel;
import vidada.viewmodel.tags.TagViewModel;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.services.ISelectionManager;
import archimedesJ.services.SelectionManager;


/**
 * MediaBrowserModel for the MediaBrowserFX
 * @author IsNull
 *
 */
public class MediaBrowserModel {

	private final ISelectionManager<IBrowserItem> selectionManager = new SelectionManager<IBrowserItem>();
	private final List<IBrowserItem> medias = new ArrayList<IBrowserItem>();
	private final ITagStatesVM tagStatesModel = new TagStatesModel(TagViewModel.VMFactory);


	//
	// Events
	//
	private final EventHandlerEx<CollectionEventArg<IBrowserItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<IBrowserItem>>();
	/**
	 * Raised when the media model has changed
	 */
	public IEvent<CollectionEventArg<IBrowserItem>> getMediasChangedEvent() {return mediasChangedEvent;}


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


	public IBrowserItem getItem(int index){
		return medias.get(index);
	}

	/**
	 * Gets a copy of the internal media list
	 * @return
	 */
	public List<IBrowserItem> getSnapshot(){
		return new ArrayList<IBrowserItem>(medias);
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
	public List<IBrowserItem> getRaw() {
		return medias;
	}

	/**
	 * Gets the selection manager
	 * @return
	 */
	public ISelectionManager<IBrowserItem> getSelectionManager() {
		return selectionManager;
	}



}
