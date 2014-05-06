package vidada.client.model.browser;

import archimedes.core.data.IDataProvider;
import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import vidada.model.media.MediaItem;

import java.util.List;

/**å
 * Represents a folder-node of IBrowserItems
 * @author IsNull
 *
 */
public abstract class BrowserFolderItem implements IBrowserItem, IDataProvider<IBrowserItem> {

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


	/**
	 * Load all children of this folder
	 * @return
	 */
	protected abstract List<IBrowserItem> loadChildren();

	/**
	 * Get the name of this folder
	 */
	@Override
	public abstract String getName();


	/**
	 * Since this is a folder, this will always return true.
	 */
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

	@Override
	public String toString(){
		return "[" + getName() + "]";
	}


}
