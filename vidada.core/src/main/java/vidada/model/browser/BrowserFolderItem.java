package vidada.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

/**
 * Represents a folder-node of IBrowserItems
 * @author IsNull
 *
 */
public abstract class BrowserFolderItem implements IBrowserItem, IListProvider<IBrowserItem>{

	private List<IBrowserItem> children = null;

	transient private final EventHandlerEx<EventArgs> openRequestEvent = new EventHandlerEx<EventArgs>();
	public IEvent<EventArgs> getOpenRequestEvent() { return openRequestEvent; }

	private final EventHandlerEx<CollectionEventArg<IBrowserItem>> mediasChangedEvent = new EventHandlerEx<CollectionEventArg<IBrowserItem>>();

	@Override
	public IEvent<CollectionEventArg<IBrowserItem>> getItemsChangedEvent() { return mediasChangedEvent; }


	/**
	 * Create a new BrowserFolderItem
	 */
	protected BrowserFolderItem() { } 


	protected abstract List<IBrowserItem> loadChildren();

	/**
	 * Get the name of this folder
	 */
	@Override
	public abstract String getName();


	@Override
	public final boolean isFolder() {
		return true;
	}

	@Override
	public List<IBrowserItem> getChildren() {
		if(children == null) 
			children = loadChildren();
		return children;
	}

	@Override
	public final MediaItem getData() { return null; }

	public void requestOpen() {
		openRequestEvent.fireEvent(this, EventArgs.Empty);
	}

	//
	// Implementation of IListProvider<IBrowserItem>
	//


	@Override
	public IBrowserItem get(int index) {
		return getChildren().get(index);
	}

	@Override
	public int size() {
		return getChildren().size();
	}

	@Override
	public boolean isEmpty() {
		return getChildren().isEmpty();
	}



}
