package vidada.viewsFX.breadcrumbs;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventListenerEx;

public class BreadCrumbBar extends HBox {

	private BreadCrumbBarModel model;

	public BreadCrumbBar(){

	}

	public void setDataContext(BreadCrumbBarModel model){
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
			System.out.println("BreadCrumbBar::updateView with " + model.size() + " items");
			for (int i=0; model.size() > i; i++) {

				boolean first = i==0;
				BreadCrumbButton item = createCrumb(model.get(i), first);

				int ins = item.getArrowWidth() / 2;
				double right = 0;
				double left = 0;

				if(!first){
					right = -ins;
					left = right;
				}else{
					right = -ins;
				}

				//Insets double top, double right, double bottom, double left
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
