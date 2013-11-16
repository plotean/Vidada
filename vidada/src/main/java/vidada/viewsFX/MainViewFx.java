package vidada.viewsFX;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.controller.filters.MediaFilterController;
import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.FilterModel;
import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.IVMFactory;
import vidada.viewmodel.MediaBrowserModel;
import vidada.viewmodel.media.IMediaViewModel;
import vidada.viewmodel.media.MediaDetailViewModel;
import vidada.viewmodel.tags.TagServiceModelBinding;
import vidada.viewmodel.tags.TagStatesModel;
import vidada.viewmodel.tags.TagViewModel;
import vidada.viewsFX.filters.FilterViewFx;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import vidada.viewsFX.medias.MediaDetailViewFx;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.services.ISelectionManager;

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

	private final ITagService tagService = ServiceProvider.Resolve(ITagService.class);
	private final ITagStatesVM mediaDetailTagstates = new TagStatesModel(new IVMFactory<Tag, TagViewModel>() {
		@Override
		public TagViewModel create(Tag model) {
			return new TagViewModel(model, TagState.Allowed,  TagState.Required);
		}
	});
	private MediaDetailViewModel singleMediaDetailVM = new MediaDetailViewModel(mediaDetailTagstates);


	private MediaFilterController filterController;


	public MainViewFx(){

		// TODO refactor this out into main-context-model
		browserModel = new MediaBrowserModel();
		TagServiceModelBinding.bind(tagService, browserModel.getTagStatesModel());
		filterModel = new FilterModel(browserModel.getTagStatesModel());
		filterController = new MediaFilterController(filterModel, browserModel);


		TagServiceModelBinding.bind(tagService, mediaDetailTagstates);

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

		final MediaDetailViewFx detailView = new MediaDetailViewFx();


		TitledPane detailPane = new TitledPane("Detail", detailView);
		detailView.setPadding(new Insets(10));
		mediaBrowserFX.setBottom(detailPane);

		browserModel.getSelectionManager().getSelectionChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				IMediaViewModel vm = getMediaDetailVM(browserModel.getSelectionManager());	
				detailView.setDataContext(vm);
			}
		});

		AquaFx.createTabPaneStyler().setType(TabPaneType.REGULAR).style(mainTab);
		return mainTab;
	}


	private  IMediaViewModel getMediaDetailVM(ISelectionManager<MediaItem> mediaSelection){
		if(mediaSelection.getSelection().size() <= 1){

			singleMediaDetailVM.setModel(mediaSelection.getFirstSelected());

			return singleMediaDetailVM;

		}else{
			// currently only single selection supported
			throw new NotImplementedException("MainViewFx: Multiple Selection not supported!");
		}
	}


}
