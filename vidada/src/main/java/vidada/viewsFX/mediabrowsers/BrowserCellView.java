package vidada.viewsFX.mediabrowsers;

import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import vidada.client.viewmodel.browser.BrowserItemVM;

/**
 * Represents a View for a single item in the media Browser
 * @author IsNull
 *
 */
public abstract class BrowserCellView extends BorderPane {

	private BrowserItemVM viewmodel;

	public BrowserCellView(){
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedSelectionHandler);

	}


	public void setDataContext(BrowserItemVM viewmodel){
		this.viewmodel = viewmodel;
	}

	public BrowserItemVM getDataContext(){
		return viewmodel;
	}


	/**
	 * Create the context menu for this cell
	 * @return
	 */
	protected abstract ContextMenu createContextMenu();

	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseClickedSelectionHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				if(me.getClickCount() == 1){
					BrowserItemVM itemVM = getDataContext();
					if(itemVM != null)
						itemVM.setSelected(true);
				}
			} else if(me.getButton().equals(MouseButton.SECONDARY)){

				if(me.getClickCount() == 1){
					ContextMenu menu = createContextMenu();
					if(menu != null){
						//contextMenu.show(MediaItemView.this, me.getX(), me.getY());
						menu.show(BrowserCellView.this, Side.RIGHT, 0,0);
					}
				}
			}
		}
	};
}
