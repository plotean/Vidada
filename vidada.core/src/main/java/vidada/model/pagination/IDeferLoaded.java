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
	 * Returns the loaded object or <code>null</code> if it has not been loaded.
	 * @return
	 */
	T getLoadedItem();

	/**
	 * Returns true if the object has been loaded, false otherwise
	 * @return
	 */
	boolean isLoaded();
}
