package vidada.viewsFX.breadcrumbs;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import vidada.model.browser.IDataProvider;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventListenerEx;

/**
 * Represents a BreadCrumbBar
 * 
 * @author IsNull
 *
 */
public class BreadCrumbBar extends HBox {

	private IDataProvider<IBreadCrumbModel> model;

	public BreadCrumbBar(){

	}

	/**
	 * Set the data context of this view
	 * @param model
	 */
	public void setDataContext(IDataProvider<IBreadCrumbModel> model){
		if(this.model != null){
			this.model.getItemsChangedEvent().remove(itemsChangedListener);
		}

		this.model = model;

		if(model != null){
			model.getItemsChangedEvent().add(itemsChangedListener);
		}

		updateView();
	}

	private void updateView(){

		this.getChildren().clear();

		if(model != null){
			for (int i=0; model.size() > i; i++) {

				boolean first = i==0;
				BreadCrumbButton item = createCrumb(model.get(i), first);

				// We have to position the bread crumbs slightly overlapping
				// thus we have to create negative Insets
				double ins = item.getArrowWidth() / 2.0;
				double right = -ins - 0.1d;
				double left = (!first) ? right : 0; // Omit the first button

				HBox.setMargin(item, new Insets(0, right, 0, left));
				this.getChildren().add(item);
			}
		}
	}

	private BreadCrumbButton createCrumb(IBreadCrumbModel model, boolean home){
		return new BreadCrumbButton(model.getName(), home);
	}


	private final EventListenerEx<CollectionEventArg<IBreadCrumbModel>> itemsChangedListener =
			new EventListenerEx<CollectionEventArg<IBreadCrumbModel>>() {
		@Override
		public void eventOccured(Object sender, CollectionEventArg<IBreadCrumbModel> eventArgs) {
			updateView();
		}
	};
}
