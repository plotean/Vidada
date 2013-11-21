package vidada.viewsFX.mediabrowsers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.beans.property.DoubleProperty;

import org.controlsfx.control.GridCell;

import vidada.viewmodel.MediaViewModel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;

class MediaGridItemCell extends GridCell<MediaViewModel> {

	private final MediaItemView visual; 
	private static Method setSelectionMethod;


	static {
		for (Method m : javafx.scene.control.Cell.class.getDeclaredMethods()) {
			if(m.getName().equals("setSelected")){
				setSelectionMethod = m;
				setSelectionMethod.setAccessible(true);
			}
		}
	}

	private static boolean tog = false;

	public MediaGridItemCell(MediaItemView mediaView, DoubleProperty desiredWidth, DoubleProperty desiredHeight){
		visual = mediaView;

		setId("media-cell");

		this.prefWidthProperty().bind(desiredWidth);
		this.prefHeightProperty().bind(desiredHeight);
		this.maxWidthProperty().bind(desiredWidth);
		this.maxHeightProperty().bind(desiredHeight);
		this.minWidthProperty().bind(desiredWidth);
		this.minHeightProperty().bind(desiredHeight);


		setGraphic(visual);
	}


	private void setSelectionInternal(boolean selected){
		if(setSelectionMethod != null){

			try {
				setSelectionMethod.invoke(this, selected);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private MediaViewModel item;

	@Override
	public void updateItem(MediaViewModel item, boolean empty) {

		if(this.item != null) 
			this.item.SelectionChangedEvent.remove(selectionListener);

		super.updateItem(item, empty);
		visual.setDataContext(item);

		this.item = item;
		updateSelection();

		if(item != null) item.SelectionChangedEvent.add(selectionListener);
	}

	private final EventListenerEx<EventArgs> selectionListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			updateSelection();
		}
	};

	private void updateSelection(){
		if(item != null){
			if( isSelected() != item.isSelected()){
				setSelectionInternal(item.isSelected());
				System.out.println("MediaGridItemCell: Selected: " + isSelected() + " - " + item + " Cell" + this.hashCode() );
			}
		}else{
			setSelectionInternal(false);
		}
	}
}
