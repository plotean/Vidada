package vidada.model.pagination;

import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.IEvent;

/**
 * Generic read-only observable data provider
 * @author IsNull
 *
 * @param <T>
 */
public interface IDataProvider<T> {

	/**
	 * Get the ItemChangedEvent
	 * @return
	 */
	public IEvent<CollectionEventArg<T>> getItemsChangedEvent();

	/**
	 * Get the item at given index
	 * @param index
	 * @return
	 */
	public T get(int index);

	/**
	 * Get the amount of items
	 * @return
	 */
	public int size();

	/**
	 * Returns true if there is no data available
	 * @return
	 */
	public boolean isEmpty();
}
