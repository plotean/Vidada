package vidada.viewsFX.mediabrowsers;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vidada.viewmodel.browser.BrowserFolderItemVM;
import vidada.viewmodel.browser.BrowserItemVM;


public class FolderView extends BrowserCellView {

	private final Label description = new Label("<no description>");
	private BrowserFolderItemVM viewmodel;

	public FolderView(){

		description.setId("description"); // style id
		description.setAlignment(Pos.CENTER_LEFT);
		description.setPadding(new Insets(10));

		this.setBottom(description);
	}


	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseOpenHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				if(viewmodel != null){
					viewmodel.open();
				}
			}
		}
	};




	@Override
	public void setDataContext(BrowserItemVM viewmodel) {
		super.setDataContext(viewmodel);

		if(viewmodel == null || viewmodel instanceof BrowserFolderItemVM){
			this.viewmodel = (BrowserFolderItemVM)viewmodel;

			if(viewmodel != null){
				description.setText(viewmodel.getName());
			}
		}	
	}

	@Override
	protected ContextMenu createContextMenu() {
		// TODO Auto-generated method stub
		return null;
	}
}
