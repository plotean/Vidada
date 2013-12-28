package vidada.viewsFX.mediabrowsers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
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
