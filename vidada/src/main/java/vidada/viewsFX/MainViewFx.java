package vidada.viewsFX;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import vidada.model.ServiceProvider;
import vidada.model.browser.MediaBrowserModel;
import vidada.model.tags.ITagService;
import vidada.viewmodel.explorer.MediaExplorerVM;
import vidada.viewsFX.mediaexplorer.PrimaryMediaExplorerFX;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;

/**
 * Represents the main view
 * @author IsNull
 *
 */
public class MainViewFx extends BorderPane {

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);
	private final MediaBrowserModel browserModel;
	private final MediaExplorerVM mediaExplorerVM;


	public MainViewFx(){

		// TODO refactor this out into main-context-model
		browserModel = new MediaBrowserModel();

		mediaExplorerVM = new MediaExplorerVM();

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

		browserTab.setContent(new PrimaryMediaBrowserFX(browserModel, tagService));

		PrimaryMediaExplorerFX mediaExplorerFX = new PrimaryMediaExplorerFX();
		mediaExplorerFX.setDataContext(mediaExplorerVM);
		fileBrowserTab.setContent(mediaExplorerFX);

		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}

}
