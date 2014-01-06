package vidada.viewsFX.breadcrumbs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vidada.model.browser.IDataProvider;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.util.Lists;


public class BreadCrumbBarModel implements IDataProvider<IBreadCrumbModel> {

	private final List<IBreadCrumbModel> breadCrumbs = new ArrayList<IBreadCrumbModel>();
	private final EventHandlerEx<CollectionEventArg<IBreadCrumbModel>> ItemsChangedEvent = new EventHandlerEx<CollectionEventArg<IBreadCrumbModel>>();

	@Override
	public IEvent<CollectionEventArg<IBreadCrumbModel>> getItemsChangedEvent() { return ItemsChangedEvent; }

	public void add(Collection<IBreadCrumbModel> items){
		breadCrumbs.addAll(items);
		ItemsChangedEvent.fireEvent(this, 
				new CollectionEventArg<IBreadCrumbModel>(CollectionChangeType.Added, items.toArray(new IBreadCrumbModel[0])));

	}

	public void add(IBreadCrumbModel... item){
		add(Lists.asNoNullList(item));
	}


	@Override
	public IBreadCrumbModel get(int index) {
		return breadCrumbs.get(index);
	}

	@Override
	public int size() {
		return breadCrumbs.size();
	}

	@Override
	public boolean isEmpty() {
		return breadCrumbs.isEmpty();
	}
}
