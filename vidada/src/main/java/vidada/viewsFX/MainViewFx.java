package vidada.viewsFX;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import vidada.viewmodels.MainViewModel;
import vidada.viewsFX.mediaexplorer.PrimaryMediaExplorerFX;

/**
 * Represents the main view
 * @author IsNull
 *
 */
public class MainViewFx extends BorderPane {


    private MainViewModel mainViewModel = new MainViewModel();


	public MainViewFx(){

		// create the view
		this.setTop(new VidadaToolBar());
		this.setCenter(createMainContent());
	}


	private Node createMainContent(){	

		TabPane mainTab = new TabPane();

		Tab browserTab = new Tab("Browser");
		Tab fileBrowserTab = new Tab("File Browser");
		browserTab.setClosable(false);
		fileBrowserTab.setClosable(false);
		mainTab.getTabs().add(browserTab);
		mainTab.getTabs().add(fileBrowserTab);

        //private final ITagClientService tagService = clientManager.getActive().getTagClientService();
        //private final IMediaClientService mediaService = clientManager.getActive().getMediaClientService();

		browserTab.setContent(new PrimaryMediaBrowserFX(mainViewModel.getPrimaryMediaBrowserVM()));

		PrimaryMediaExplorerFX mediaExplorerFX = new PrimaryMediaExplorerFX();
		mediaExplorerFX.setDataContext(mainViewModel.getMediaExplorerVM());
		fileBrowserTab.setContent(mediaExplorerFX);

		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}

}
