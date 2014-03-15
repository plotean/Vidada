package vidada.model.pagination;

import java.util.ArrayList;
import java.util.List;

import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

/**
 * Virtual data source adapter
 * 
 * @author IsNull
 *
 * @param <T> Target type
 * @param <ST> Source type
 */
public class DataProviderTransformer<T, ST> implements IDataProvider<T> {

	public static interface ITransform<T,ST> { T transform(ST source); }

	private final IDataProvider<ST> source;
	private final ITransform<T,ST> transformer;


	private final EventHandlerEx<CollectionEventArg<T>> itemsChangedEvent = new EventHandlerEx<CollectionEventArg<T>>();
	@Override
	public IEvent<CollectionEventArg<T>> getItemsChangedEvent() { return itemsChangedEvent; }

	/**
	 * Creates a new DataProviderTransformer
	 * @param source Source list which gets JIT transformed
	 * @param transformer The transformer
	 */
	public DataProviderTransformer(IDataProvider<ST> source, ITransform<T,ST> transformer){
		this.source = source;
		this.transformer = transformer;

		this.source.getItemsChangedEvent().add(listener);
	}

	private final EventListenerEx<CollectionEventArg<ST>> listener = new EventListenerEx<CollectionEventArg<ST>>() {
		@Override
		public void eventOccured(Object sender, CollectionEventArg<ST> eventArgs) {

			List<T> evItems = new ArrayList<T>();
			for (ST sitem : eventArgs.getItems()) {
				evItems.add(transformer.transform(sitem));
			}

			itemsChangedEvent.fireEvent(this, 
					new CollectionEventArg<T>(eventArgs.getType(), evItems));
		}
	};

	@Override
	public T get(int index) {
		ST objSt = source.get(index);
		return objSt != null ? transformer.transform(objSt) : null;
	}

	@Override
	public int size() {
		return source.size();
	}

	@Override
	public boolean isEmpty() {
		return source.isEmpty();
	}
}
