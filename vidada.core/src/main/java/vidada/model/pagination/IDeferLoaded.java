package vidada.model.pagination;

import archimedesJ.events.EventArgs;
import archimedesJ.events.IEvent;

/**
 * Represents an object T which can be loaded later.
 * 
 * @author IsNull
 *
 * @param <T>
 */
public interface IDeferLoaded<T> {

	/**
	 * Event which is fired when this object has been loaded.
	 * @return
	 */
	IEvent<EventArgs> getLoadedEvent();

	/**
	 * Returns the loaded object
	 * @return
	 */
	T getLoadedItem();

	/**
	 * Is the object loaded?
	 * @return
	 */
	boolean isLoaded();
}
