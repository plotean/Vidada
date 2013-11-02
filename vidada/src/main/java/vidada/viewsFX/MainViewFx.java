package vidada.viewsFX;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.controller.filters.MediaFilterController;
import vidada.model.media.MediaBrowserModel;
import vidada.viewsFX.filters.FilterViewFx;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;

public class MainViewFx extends BorderPane {

	public MainViewFx(){
		this.setTop(new VidadaToolBar());
		this.setCenter(createMainContent());
	}

	MediaBrowserModel browserModel = new MediaBrowserModel();

	MediaFilterController filterController;

	private Node createMainContent(){	

		TabPane mainTab = new TabPane();

		Tab browserTab = new Tab("Browser");
		Tab fileBrowserTab = new Tab("File Browser");
		browserTab.setClosable(false);
		fileBrowserTab.setClosable(false);
		mainTab.getTabs().add(browserTab);
		mainTab.getTabs().add(fileBrowserTab);

		MediaBrowserFX mediaBrowserFX = new MediaBrowserFX();
		browserTab.setContent(mediaBrowserFX);


		// TODO: Controller?
		mediaBrowserFX.setDataContext(browserModel);

		FilterViewFx filterView = new FilterViewFx();


		TitledPane filterPane = new TitledPane("Filter", filterView);
		mediaBrowserFX.setTop(filterPane);
		filterController = new MediaFilterController(filterView, browserModel);

		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}
}
