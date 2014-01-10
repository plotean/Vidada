package vidada.viewsFX.breadcrumbs;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Represents a BreadCrumbBar
 * 
 * @author IsNull
 *
 */
public class BreadCrumbBar<T extends IBreadCrumbModel> extends HBox {

	public static interface BreadCrumbOpenListener<I> { 
		void openBreadCrumb(I crumb); 
	}

	public static interface BreadCrumbNodeFactory<T> { 
		BreadCrumbButton createBreadCrumbButton(T crumb, int index); 
	}

	private final List<BreadCrumbOpenListener<T>> openListeners = new ArrayList<>();
	private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<ObservableList<T>>(this, "items");
	private final ObjectProperty<BreadCrumbNodeFactory<T>> crumbFactory = new SimpleObjectProperty<BreadCrumbNodeFactory<T>>(this, "crumbFactory");


	/**
	 * Create a new BreadCrumbBar
	 */
	public BreadCrumbBar(){ 
		items.addListener(new ChangeListener<ObservableList<T>>() {
			@Override
			public void changed(
					ObservableValue<? extends ObservableList<T>> observable,
							ObservableList<T> oldValue, ObservableList<T> newValue) {

				if(oldValue != null) 
					oldValue.removeListener(itemsListener);

				if(newValue != null)
					newValue.addListener(itemsListener);

				updateView();
			}
		});

		crumbFactory.addListener(new ChangeListener<BreadCrumbNodeFactory<T>>() {
			@Override
			public void changed(
					ObservableValue<? extends BreadCrumbNodeFactory<T>> observable,
							BreadCrumbNodeFactory<T> oldValue, BreadCrumbNodeFactory<T> newValue){ 
				updateView();
			}
		});

	}

	transient final private BreadCrumbNodeFactory<T> breadCrumbNodeFactoryDefault = new BreadCrumbNodeFactory<T>() {
		@Override
		public BreadCrumbButton createBreadCrumbButton(T crumb, int index) {
			return  new BreadCrumbButton(crumb.getName(), index == 0);
		}
	};


	public void addOpenListener(BreadCrumbOpenListener<T> listener){
		openListeners.add(listener);
	}

	public void removeOpenListener(BreadCrumbOpenListener<T> listener){
		openListeners.remove(listener);
	}

	protected void fireBreadCrumbOpen(T crumb){
		for (BreadCrumbOpenListener<T> listener : openListeners) {
			listener.openBreadCrumb(crumb);
		}
	}


	transient private final ListChangeListener<T> itemsListener = new ListChangeListener<T>(){
		@Override
		public void onChanged(ListChangeListener.Change<? extends T> arg) {
			// TODO Optimize depending on actual change event
			updateView();
		}
	};


	public final ObjectProperty<ObservableList<T>> itemsProperty() {
		return items;
	}

	/**
	 * Set the BreadCrumb factory used to create all the crumbs in the bar
	 * @param factory
	 */
	public final void setCrumbFactory(BreadCrumbNodeFactory<T> factory){
		crumbFactory.setValue(factory);
	}

	public final BreadCrumbNodeFactory<T> getCrumbFactory(){
		return crumbFactory.getValue();
	}

	/**
	 * Sets a new {@link ObservableList} as the items list underlying GridView.
	 * The old items list will be discarded.
	 */
	public final void setItems(ObservableList<T> value) {
		itemsProperty().set(value);
	}

	/**
	 * Returns the currently-in-use items list that is being used by the
	 * BreadCrumbBar.
	 */
	public final ObservableList<T> getItems() {
		return items == null ? null : items.get();
	}


	protected void updateView(){

		this.getChildren().clear();

		ObservableList<T> items = getItems();
		if(items != null){
			for (int i=0; items.size() > i; i++) {

				BreadCrumbButton item = createCrumb(items.get(i), i);

				if(item != null){
					// We have to position the bread crumbs slightly overlapping
					// thus we have to create negative Insets
					double ins = item.getArrowWidth() / 2.0;
					double right = -ins - 0.1d;
					double left = !(i==0) ? right : 0; // Omit the first button

					HBox.setMargin(item, new Insets(0, right, 0, left));
					this.getChildren().add(item);
				}
			}
		}
	}

	private final BreadCrumbButton createCrumb(final T crumbModel, int index){
		BreadCrumbNodeFactory<T> factory = getCrumbFactory();
		if(factory == null){
			// fall back to default factory
			factory = breadCrumbNodeFactoryDefault;
		}

		BreadCrumbButton crumb = factory.createBreadCrumbButton(crumbModel, index);

		if(crumb != null){

			// We want all buttons to have the same height
			// so we bind their preferred height to this enclosing container
			crumb.prefHeightProperty().bind(this.heightProperty());

			crumb.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent me) {
					if(me.getButton().equals(MouseButton.PRIMARY)){
						crumbModel.open();
						fireBreadCrumbOpen(crumbModel);
					}
				}
			});
		}

		return crumb;
	}
}
