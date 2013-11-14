package vidada.viewsFX;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.controller.filters.MediaFilterController;
import vidada.model.ServiceProvider;
import vidada.model.browser.FilterModel;
import vidada.model.browser.MediaBrowserModel;
import vidada.model.browser.TagServiceModelBinding;
import vidada.model.tags.ITagService;
import vidada.viewsFX.filters.FilterViewFx;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import vidada.viewsFX.medias.MediaDetailViewFx;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.TabPaneType;

/**
 * Represents the main view
 * @author IsNull
 *
 */
public class MainViewFx extends BorderPane {

	private final FilterModel filterModel;
	private final MediaBrowserModel browserModel;


	private MediaFilterController filterController;


	public MainViewFx(){

		// TODO refactor this out into main-context-model
		browserModel = new MediaBrowserModel();
		ITagService tagService = ServiceProvider.Resolve(ITagService.class);
		TagServiceModelBinding.bind(tagService, browserModel.getTagStatesModel());
		filterModel = new FilterModel(browserModel.getTagStatesModel());

		filterController = new MediaFilterController(filterModel, browserModel);

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

		MediaBrowserFX mediaBrowserFX = new MediaBrowserFX();
		browserTab.setContent(mediaBrowserFX);


		mediaBrowserFX.setDataContext(browserModel);


		FilterViewFx filterView = new FilterViewFx(filterModel);


		TitledPane filterPane = new TitledPane("Filter", filterView);
		mediaBrowserFX.setTop(filterPane);

		MediaDetailViewFx detailView = new MediaDetailViewFx();

		TitledPane detailPane = new TitledPane("Detail", detailView);
		mediaBrowserFX.setBottom(detailPane);


		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}
}
