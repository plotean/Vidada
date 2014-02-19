package vidada.viewsFX.mediabrowsers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.beans.property.DoubleProperty;

import org.controlsfx.control.GridCell;

import vidada.client.viewmodel.MediaViewModel;
import vidada.client.viewmodel.browser.BrowserFolderItemVM;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.viewsFX.player.IMediaPlayerService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;

class MediaGridItemCell extends GridCell<BrowserItemVM> {

	private final MediaItemView mediaView; 
	private final FolderView folderView;
	private final SimpleCellItemView simpleView;

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

	public MediaGridItemCell(IMediaPlayerService mediaPlayerService, DoubleProperty desiredWidth, DoubleProperty desiredHeight){

		mediaView = new MediaItemView(mediaPlayerService);
		folderView = new FolderView();
		simpleView = new SimpleCellItemView();

		setId("media-cell");

		this.prefWidthProperty().bind(desiredWidth);
		this.prefHeightProperty().bind(desiredHeight);
		this.maxWidthProperty().bind(desiredWidth);
		this.maxHeightProperty().bind(desiredHeight);
		this.minWidthProperty().bind(desiredWidth);
		this.minHeightProperty().bind(desiredHeight);

		setGraphic(mediaView);
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

	private BrowserItemVM item;

	@Override
	public void updateItem(BrowserItemVM item, boolean empty) {

		if(this.item != null) 
			this.item.SelectionChangedEvent.remove(selectionListener);

		super.updateItem(item, empty);

		updateVisualTemplate(item);

		this.item = item;
		updateSelection();

		if(item != null) item.SelectionChangedEvent.add(selectionListener);
	}

	private void updateVisualTemplate(BrowserItemVM item){

		if(mediaView != null) mediaView.setDataContext(null);
		if(folderView != null) folderView.setDataContext(null);
		if(simpleView != null) simpleView.setDataContext(null);

		if(item instanceof MediaViewModel){
			mediaView.setDataContext(item);
			setGraphic(mediaView);
		}else if(item instanceof BrowserFolderItemVM){
			folderView.setDataContext(item);
			setGraphic(folderView);
		}else{
			simpleView.setDataContext(item);
			setGraphic(simpleView);
		}
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
