package vidada.model.browser;

import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.IEvent;

public interface IListProvider<T> {


	public IEvent<CollectionEventArg<IBrowserItem>> getItemsChangedEvent();

	public T get(int index);

	public int size();

	public boolean isEmpty();
}
