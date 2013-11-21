package vidada.viewsFX.mediabrowsers;


import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.model.media.MediaItem;
import vidada.viewmodel.IVMFactory;
import vidada.viewmodel.MediaBrowserModel;
import vidada.viewmodel.MediaViewModel;
import vidada.viewmodel.ViewModelPool;
import vidada.viewsFX.player.IMediaPlayerService;
import vlcj.fx.MediaPlayerService;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;

/**
 * Represents the media browser
 * @author IsNull
 *
 */
public class MediaBrowserFX extends BorderPane {

	private MediaBrowserModel mediaModel;
	private GridView<MediaViewModel> gridView;
	private GridViewViewPort gridViewPort;
	private IndexRange visibleCells;

	transient private final ObservableList<MediaViewModel> observableMedias;
	transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


	public MediaBrowserFX(){
		observableMedias = FXCollections.observableArrayList(new ArrayList<MediaViewModel>());
		initView();		
	}

	private void initView(){
		gridView = new GridView<MediaViewModel>();

		gridViewPort = new GridViewViewPort(gridView);
		gridViewPort.getViewPortItemsChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				onVisibleItemsChanged();
			}
		});

		gridView.setStyle("-fx-background-color: #FFFFFF;");

		gridView.getStylesheets().add("css/default_media_cell.css");

		gridView.setCellFactory(new Callback<GridView<MediaViewModel>, GridCell<MediaViewModel>>() {
			@Override
			public GridCell<MediaViewModel> call(GridView<MediaViewModel> arg0) {

				return new MediaGridItemCell(
						new MediaItemView(mediaPlayerService),
						gridView.cellWidthProperty(),
						gridView.cellHeightProperty()
						);
			}
		});


		setItemSize(200, 140);

		gridView.setItems(observableMedias);

		this.setCenter(gridView);
	}


	public void setItemSize(double width, double height){
		gridView.setCellWidth(width);
		gridView.setCellHeight(height);
	}



	/**
	 * Occurs when the items visible in the current ViewPort have been changed
	 */
	private void onVisibleItemsChanged(){

		IndexRange currentVisibleCells = gridViewPort.getVisibleCellRange();
		int[] dropped = IndexRange.unused(visibleCells, currentVisibleCells);

		if(dropped.length != 0){
			System.out.print( visibleCells + " - " + currentVisibleCells + " Droped Cells: {");
			for (int i : dropped) {
				System.out.print(i+";");
			}
			System.out.println("}");
		}

		visibleCells = currentVisibleCells;
	}

	public void setDataContext(MediaBrowserModel mediaModel){

		if(this.mediaModel != null){
			this.mediaModel.getMediasChangedEvent().remove(mediasChangedEventListener);
		}

		this.mediaModel = mediaModel;

		if(mediaModel != null){
			mediaModel.getMediasChangedEvent().add(mediasChangedEventListener);
		}

		updateView();
	}



	transient private EventListenerEx<CollectionEventArg<MediaItem>> mediasChangedEventListener 
	= new EventListenerEx<CollectionEventArg<MediaItem>>() {
		@Override
		public void eventOccured(Object sender,
				CollectionEventArg<MediaItem> eventArgs) {
			updateView();
		}
	};

	private MediaViewModel previousSelection;

	transient private final EventListenerEx<EventArgs> VMselectionChangedListener = new EventListenerEx<EventArgs>() {

		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {

			MediaViewModel vm = (MediaViewModel)sender;

			if(previousSelection != null) previousSelection.setSelected(false);

			mediaModel.getSelectionManager().trySelect(vm.getModel());

			previousSelection = vm;
		}
	};




	private IVMFactory<MediaItem, MediaViewModel> vmFactory = new IVMFactory<MediaItem, MediaViewModel>() {
		@Override
		public MediaViewModel create(MediaItem model) {
			MediaViewModel vm = new MediaViewModel(model);
			vm.SelectionChangedEvent.add(VMselectionChangedListener);

			gridViewPort.ensureViewportChangedListener(); // Very hacky. (Should be called once after Scene is shown)

			return vm;
		}
	};

	transient private final ViewModelPool<MediaItem, MediaViewModel> mediaVMPool =
			new ViewModelPool<MediaItem, MediaViewModel>(vmFactory);


	private synchronized void updateView(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				observableMedias.clear();

				if(mediaModel != null){

					final List<MediaItem> items = mediaModel.getRaw();
					System.out.println("MediaBrowserFX:updateView items: " + items.size() + " empty?" + items.isEmpty());

					final List<MediaViewModel> itemVMs = new ArrayList<>();
					for (MediaItem mediaItem : items) {
						itemVMs.add(mediaVMPool.viewModel(mediaItem));
					}
					observableMedias.addAll(itemVMs);
				}else {
					System.out.println("medias empty");
				}
			}
		});
	}


}
