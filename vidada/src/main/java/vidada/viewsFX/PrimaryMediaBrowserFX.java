package vidada.viewsFX;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.client.IVidadaClientManager;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.viewmodel.FilterModel;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.client.viewmodel.media.MediaDetailViewModel;
import vidada.controller.filters.MediaFilterDatabaseBinding;
import vidada.viewsFX.filters.FilterViewFx;
import vidada.viewsFX.mediabrowsers.MediaBrowserFX;
import vidada.viewsFX.medias.MediaDetailViewFx;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.services.ISelectionManager;

/**
 * Represents the main media browser
 * @author IsNull
 *
 */
public class PrimaryMediaBrowserFX extends BorderPane {

	private final FilterModel filterModel;

	private final ITagClientService tagClientService;
	private final MediaBrowserModel browserModel;
	//private final MediaDetailViewModel singleMediaDetailVM;


	/*
	private final ITagStatesVM mediaDetailTagstates = new TagStatesModel(new IVMFactory<Tag, TagViewModel>() {
		@Override
		public TagViewModel create(Tag model) {
			return new TagViewModel(model, TagState.Allowed,  TagState.Required);
		}
	});*/



	public PrimaryMediaBrowserFX(MediaBrowserModel browserModel, IVidadaClientManager serverClientService, ITagClientService tagClientService, IMediaClientService mediaService){
		this.tagClientService = tagClientService;
		this.browserModel = browserModel;
		this.filterModel  = new FilterModel(mediaService);;

		//singleMediaDetailVM = new MediaDetailViewModel(tagService);

		// bind the different models together

		//TagServiceModelBinding.bind(tagClientService, browserModel.getTagStatesModel());
		//TagServiceModelBinding.bind(tagClientService, mediaDetailTagstates);
		MediaFilterDatabaseBinding.bind(serverClientService, mediaService, filterModel, browserModel);

		// setup views

		// Browser View
		final MediaBrowserFX mediaBrowserFX = new MediaBrowserFX();
		mediaBrowserFX.setDataContext(browserModel);
		this.setCenter(mediaBrowserFX);

		// Filter View
		FilterViewFx filterView = new FilterViewFx(filterModel);
		TitledPane filterPane = new TitledPane("Filter", filterView);
		this.setTop(filterPane);

		// Detail View
		final MediaDetailViewFx detailView = new MediaDetailViewFx();
		TitledPane detailPane = new TitledPane("Detail", detailView);
		detailView.setPadding(new Insets(10));
		this.setBottom(detailPane);


		mediaBrowserFX.getSelectionManager().getSelectionChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				IMediaViewModel vm = getMediaDetailVM(mediaBrowserFX.getSelectionManager());	
				detailView.setDataContext(vm);
			}
		});

	}


	private  IMediaViewModel getMediaDetailVM(ISelectionManager<IBrowserItem> mediaSelection){
		if(!mediaSelection.hasSelection()){
			return null;
		}else if(mediaSelection.getSelection().size() <= 1){
			MediaDetailViewModel vm = new MediaDetailViewModel(tagClientService);
			vm.setModel(mediaSelection.getFirstSelected());
			return vm;
		}else{
			// currently only single selection supported
			throw new NotImplementedException("MainViewFx: Multiple Selection not supported!");
		}
	}

}
