package vidada.viewsFX;

import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import vidada.controller.filters.MediaFilterDatabaseBinding;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.MediaBrowserModel;
import vidada.model.media.IMediaService;
import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import vidada.viewmodel.FilterModel;
import vidada.viewmodel.ITagStatesVM;
import vidada.viewmodel.IVMFactory;
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

/**
 * Represents the main media browser
 * @author IsNull
 *
 */
public class PrimaryMediaBrowserFX extends BorderPane {

	private final FilterModel filterModel;

	private final ITagService tagService;
	private final MediaBrowserModel browserModel;


	private final ITagStatesVM mediaDetailTagstates = new TagStatesModel(new IVMFactory<Tag, TagViewModel>() {
		@Override
		public TagViewModel create(Tag model) {
			return new TagViewModel(model, TagState.Allowed,  TagState.Required);
		}
	});

	private MediaDetailViewModel singleMediaDetailVM = new MediaDetailViewModel();


	public PrimaryMediaBrowserFX(MediaBrowserModel browserModel, ITagService tagService, IMediaService mediaService){
		this.tagService = tagService;
		this.browserModel = browserModel;
		this.filterModel  = new FilterModel(browserModel.getTagStatesModel(), mediaService);;

		// bind the different models together

		TagServiceModelBinding.bind(tagService, browserModel.getTagStatesModel());
		TagServiceModelBinding.bind(tagService, mediaDetailTagstates);
		MediaFilterDatabaseBinding.bind(filterModel, browserModel);

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
		if(mediaSelection.getSelection().size() <= 1){
			singleMediaDetailVM.setModel(mediaSelection.getFirstSelected());
			return singleMediaDetailVM;
		}else{
			// currently only single selection supported
			throw new NotImplementedException("MainViewFx: Multiple Selection not supported!");
		}
	}

}
