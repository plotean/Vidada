package vidada.viewsFX.mediabrowsers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.BorderPane;

import org.controlsfx.control.GridCell;

import vidada.client.viewmodel.MediaViewModel;
import vidada.client.viewmodel.browser.BrowserFolderItemVM;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.pagination.IDeferLoaded;
import vidada.viewsFX.player.IMediaPlayerService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;

class MediaGridItemCell extends GridCell<IDeferLoaded<BrowserItemVM>> {

	private final MediaItemView mediaView; 
	private final FolderView folderView;
	private final SimpleCellItemView simpleView;
	private final BorderPane loadingView;


	public MediaGridItemCell(IMediaPlayerService mediaPlayerService, DoubleProperty desiredWidth, DoubleProperty desiredHeight){

		mediaView = new MediaItemView(mediaPlayerService);
		folderView = new FolderView();
		simpleView = new SimpleCellItemView();
		loadingView = new BorderPane();
		loadingView.setStyle("-fx-background-color: yellow;"); // TODO: Create beautiful loading view :)


		setId("media-cell");

		this.prefWidthProperty().bind(desiredWidth);
		this.prefHeightProperty().bind(desiredHeight);
		this.maxWidthProperty().bind(desiredWidth);
		this.maxHeightProperty().bind(desiredHeight);
		this.minWidthProperty().bind(desiredWidth);
		this.minHeightProperty().bind(desiredHeight);

		setGraphic(mediaView);
	}

	private IDeferLoaded<BrowserItemVM>  item;

	@Override
	public void updateItem(IDeferLoaded<BrowserItemVM> item, boolean empty) {

		if(this.item != null && this.item.isLoaded()) 
			this.item.getLoadedItem().SelectionChangedEvent.remove(selectionListener);

		super.updateItem(item, empty);

		updateVisualTemplate(item);

		this.item = item;
		updateSelection();

		if(item != null && item.isLoaded()) 
			item.getLoadedItem().SelectionChangedEvent.add(selectionListener);
	}

	private void updateVisualTemplate(IDeferLoaded<BrowserItemVM>  item){

		if(mediaView != null) mediaView.setDataContext(null);
		if(folderView != null) folderView.setDataContext(null);
		if(simpleView != null) simpleView.setDataContext(null);

		if(item.isLoaded()){

			item.getLoadedEvent().remove(loadLlistener);

			BrowserItemVM browserItem = item.getLoadedItem();
			if(browserItem instanceof MediaViewModel){
				mediaView.setDataContext(browserItem);
				setGraphic(mediaView);
			}else if(browserItem instanceof BrowserFolderItemVM){
				folderView.setDataContext(browserItem);
				setGraphic(folderView);
			}else{
				simpleView.setDataContext(browserItem);
				setGraphic(simpleView);
			}
		}else {
			// The item is not yet loaded 
			// Listen to the load event to set the model
			item.getLoadedEvent().add(loadLlistener);

			// In the mean time, show a generic loading view
			setGraphic(loadingView);
		}
	}

	private final EventListenerEx<EventArgs> loadLlistener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			javafx.application.Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if(item != null)
						updateVisualTemplate(item);
				}
			});	
		}
	};


	//
	// TODO: Implement a proper selection model in GridView
	//
	// SELECTION HACK:
	// The GridView does currently not support any selection.
	// Thus we have to hack into this cell system until this is implemented.
	//
	private static Method setSelectionMethod;

	static {

		for (Method m : javafx.scene.control.Cell.class.getDeclaredMethods()) {
			if(m.getName().equals("setSelected")){
				setSelectionMethod = m;
				setSelectionMethod.setAccessible(true);
			}
		}
	}

	private final EventListenerEx<EventArgs> selectionListener = new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			updateSelection();
		}
	};

	private void updateSelection(){
		if(item != null && item.isLoaded()){
			if( isSelected() != item.getLoadedItem().isSelected()){
				setSelectionInternal(item.getLoadedItem().isSelected());
				System.out.println("MediaGridItemCell: Selected: " + isSelected() + " - " + item + " Cell" + this.hashCode() );
			}
		}else{
			setSelectionInternal(false);
		}
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
}
