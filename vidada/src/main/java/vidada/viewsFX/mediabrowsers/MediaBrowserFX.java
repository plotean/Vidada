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

import vidada.model.browser.BrowserFolderItem;
import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.MediaBrowserModel;
import vidada.viewmodel.IVMFactory;
import vidada.viewmodel.MediaViewModel;
import vidada.viewmodel.ViewModelPool;
import vidada.viewmodel.browser.BrowserFolderItemVM;
import vidada.viewmodel.browser.BrowserItemVM;
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
	private GridView<BrowserItemVM> gridView;
	private GridViewViewPort gridViewPort;
	private IndexRange visibleCells;

	transient private final ObservableList<BrowserItemVM> observableMedias;
	transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


	public MediaBrowserFX(){
		observableMedias = FXCollections.observableArrayList(new ArrayList<BrowserItemVM>());
		initView();		
	}

	private void initView(){
		gridView = new GridView<BrowserItemVM>();

		gridViewPort = new GridViewViewPort(gridView);
		gridViewPort.getViewPortItemsChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				onVisibleItemsChanged();
			}
		});

		gridView.setStyle("-fx-background-color: #FFFFFF;");

		gridView.getStylesheets().add("css/default_media_cell.css");

		gridView.setCellFactory(new Callback<GridView<BrowserItemVM>, GridCell<BrowserItemVM>>() {
			@Override
			public GridCell<BrowserItemVM> call(GridView<BrowserItemVM> arg0) {
				return new MediaGridItemCell(
						mediaPlayerService,
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

			for (int i : dropped) {
				if(observableMedias.size() > i){
					BrowserItemVM droppedVM = observableMedias.get(i);
					droppedVM.outsideViewPort();
				}
			}
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



	transient private EventListenerEx<CollectionEventArg<IBrowserItem>> mediasChangedEventListener 
	= new EventListenerEx<CollectionEventArg<IBrowserItem>>() {
		@Override
		public void eventOccured(Object sender,
				CollectionEventArg<IBrowserItem> eventArgs) {
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



	private IVMFactory<IBrowserItem, BrowserItemVM> vmFactory = new IVMFactory<IBrowserItem, BrowserItemVM>() {
		@Override
		public BrowserItemVM create(IBrowserItem model) {
			BrowserItemVM vm;
			if(!model.isFolder()){
				vm = new MediaViewModel((BrowserMediaItem)model);
			}else{
				BrowserFolderItem folder = (BrowserFolderItem)model;
				vm = new BrowserFolderItemVM(folder);
			}

			vm.SelectionChangedEvent.add(VMselectionChangedListener);
			gridViewPort.ensureViewportChangedListener(); // Very hacky. (Should be called once after Scene is shown)

			return vm;
		}
	};

	transient private final ViewModelPool<IBrowserItem, BrowserItemVM> mediaVMPool =
			new ViewModelPool<IBrowserItem, BrowserItemVM>(vmFactory);


	private synchronized void updateView(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				observableMedias.clear();

				if(mediaModel != null){

					final List<IBrowserItem> items = mediaModel.getRaw();
					System.out.println("MediaBrowserFX:updateView items: " + items.size() + " empty?" + items.isEmpty());

					final List<BrowserItemVM> itemVMs = new ArrayList<>();
					for (IBrowserItem item : items) {
						itemVMs.add(mediaVMPool.viewModel(item));
					}
					observableMedias.addAll(itemVMs);
				}else {
					System.out.println("medias empty");
				}
			}
		});
	}


}
