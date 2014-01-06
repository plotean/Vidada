package vidada.viewsFX.breadcrumbs;

import java.util.ArrayList;
import java.util.List;

import vidada.model.browser.IDataProvider;
import archimedesJ.data.events.CollectionChangeType;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;
import archimedesJ.util.Lists;


public class BreadCrumbBarModel implements IDataProvider<IBreadCrumbModel> {

	private final List<IBreadCrumbModel> breadCrumbs = new ArrayList<IBreadCrumbModel>();
	private final EventHandlerEx<CollectionEventArg<IBreadCrumbModel>> ItemsChangedEvent = new EventHandlerEx<CollectionEventArg<IBreadCrumbModel>>();

	private final EventHandlerEx<EventArgsG<IBreadCrumbModel>> BreadCrumbOpenEvent = new EventHandlerEx<EventArgsG<IBreadCrumbModel>>();


	/**
	 * Event raised when the items(breadcrumbs) have changed
	 */
	@Override
	public IEvent<CollectionEventArg<IBreadCrumbModel>> getItemsChangedEvent() { return ItemsChangedEvent; }

	public IEvent<EventArgsG<IBreadCrumbModel>> getBreadCrumbOpenEvent() { return BreadCrumbOpenEvent; }


	public void add(List<IBreadCrumbModel> items){

		for (IBreadCrumbModel iBreadCrumbModel : items) {
			iBreadCrumbModel.getOpenEvent().add(breadCrumbOpenRequest);
		}

		breadCrumbs.addAll(items);
		ItemsChangedEvent.fireEvent(this, 
				new CollectionEventArg<IBreadCrumbModel>(CollectionChangeType.Added, items.toArray(new IBreadCrumbModel[0])));
	}

	public void add(IBreadCrumbModel... item){
		add(Lists.asNoNullList(item));
	}

	public void clear(){
		for (IBreadCrumbModel iBreadCrumbModel : breadCrumbs) {
			iBreadCrumbModel.getOpenEvent().remove(breadCrumbOpenRequest);
		}
		breadCrumbs.clear();
		ItemsChangedEvent.fireEvent(this, CollectionEventArg.Cleared);
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

	protected void onBreadCrumbOpenRequest(IBreadCrumbModel crumb){
		BreadCrumbOpenEvent.fireEvent(this, EventArgsG.build(crumb));
	}

	private final EventListenerEx<EventArgsG<IBreadCrumbModel>> breadCrumbOpenRequest = new EventListenerEx<EventArgsG<IBreadCrumbModel>>() {
		@Override
		public void eventOccured(Object sender,
				EventArgsG<IBreadCrumbModel> eventArgs) {
			onBreadCrumbOpenRequest(eventArgs.getValue());
		}
	};
}
