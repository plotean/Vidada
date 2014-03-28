package vidada.model.pagination;

import archimedes.core.events.EventArgs;
import archimedes.core.events.IEvent;

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
