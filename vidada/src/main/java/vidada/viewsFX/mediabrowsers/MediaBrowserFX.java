package vidada.viewsFX.mediabrowsers;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.client.model.browser.BrowserFolderItem;
import vidada.client.model.browser.IBrowserItem;
import vidada.client.model.browser.MediaBrowserModel;
import vidada.client.viewmodel.browser.BrowserItemVM;
import vidada.model.pagination.IDeferLoaded;
import vidada.viewsFX.player.IMediaPlayerService;
import vidada.viewsFX.util.ObservableListFXAdapter;
import vlcj.fx.MediaPlayerService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.services.ISelectionManager;
import archimedesJ.services.SelectionManager;

/**
 * Represents the media browser
 * @author IsNull
 *
 */
public class MediaBrowserFX extends BorderPane {

	private final ISelectionManager<IBrowserItem> selectionManager = new SelectionManager<IBrowserItem>();

	private MediaBrowserModel mediaModel;
	private GridView<IDeferLoaded<BrowserItemVM>> gridView;
	private GridViewViewPort gridViewPort;
	private IndexRange visibleCells;



	//transient private final ObservableList<BrowserItemVM> observableMedias;
	transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


	public MediaBrowserFX(){
		initView();		
	}

	private void initView(){
		gridView = new GridView<IDeferLoaded<BrowserItemVM>>();

		gridViewPort = new GridViewViewPort(gridView);
		gridViewPort.getViewPortItemsChanged().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				onVisibleItemsChanged();
			}
		});

		gridView.setStyle("-fx-background-color: #FFFFFF;");

		gridView.getStylesheets().add("css/default_media_cell.css");

		gridView.setCellFactory(new Callback<GridView<IDeferLoaded<BrowserItemVM>>, GridCell<IDeferLoaded<BrowserItemVM>>>() {
			@Override
			public GridCell<IDeferLoaded<BrowserItemVM>> call(GridView<IDeferLoaded<BrowserItemVM>> control) {
				return new MediaGridItemCell(
						mediaPlayerService,
						gridView.cellWidthProperty(),
						gridView.cellHeightProperty()
						);
			}
		});


		setItemSize(200, 140);

		//gridView.setItems(observableMedias);

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

		ObservableList<IDeferLoaded<BrowserItemVM>> observableMedias = gridView.getItems();

		if(dropped.length != 0){
			for (int i : dropped) {
				if(observableMedias.size() > i){
					IDeferLoaded<BrowserItemVM> droppedVM = observableMedias.get(i);
					if(droppedVM.isLoaded())
						droppedVM.getLoadedItem().outsideViewPort();
				}
			}
		}

		visibleCells = currentVisibleCells;
	}

	public void setDataContext(MediaBrowserModel mediaModel){

		System.out.println("MediaBrowserFX: setDataContext " + mediaModel);


		selectionManager.clear();

		if(this.mediaModel != null){
			this.mediaModel.getMediaChangedEvent().remove(mediasChangedEventListener);
		}

		this.mediaModel = mediaModel;

		if(mediaModel != null){
			mediaModel.getMediaChangedEvent().add(mediasChangedEventListener);
		}

		updateView();
	}

	transient private EventListenerEx<EventArgs> mediasChangedEventListener 
	= new EventListenerEx<EventArgs>() {
		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {
			updateView();
		}
	};

	/*
	private BrowserItemVM previousSelection;

	transient private final EventListenerEx<EventArgs> VMselectionChangedListener = new EventListenerEx<EventArgs>() {

		@Override
		public void eventOccured(Object sender, EventArgs eventArgs) {

			BrowserItemVM vm = (BrowserItemVM)sender;

			if(previousSelection != null) previousSelection.setSelected(false);

			selectionManager.trySelect(vm.getModel());

			previousSelection = vm;
		}
	};



	private IVMFactory<IBrowserItem, BrowserItemVM> vmFactory = new IVMFactory<IBrowserItem, BrowserItemVM>() {
		@Override
		public BrowserItemVM create(IBrowserItem model) {
			BrowserItemVM vm;

			if(model instanceof BrowserFolderItem){
				BrowserFolderItem folder = (BrowserFolderItem)model;
				vm = new BrowserFolderItemVM(folder);
			}else if(model instanceof BrowserMediaItem){
				vm = new MediaViewModel();
				vm.setModel(model);
			}else{
				vm = new BrowserItemVM(model);
			}

			vm.SelectionChangedEvent.add(VMselectionChangedListener);

			gridViewPort.ensureViewportChangedListener(); // Very hacky. (Should be called once after Scene is shown)

			return vm;
		}
	};

	transient private final ViewModelPool<IBrowserItem, BrowserItemVM> mediaVMPool =
			new ViewModelPool<IBrowserItem, BrowserItemVM>(vmFactory);

	 */

	/**
	 * Occurs when a folderItem was opened
	 * @param folderItem
	 */
	protected void onItemFolderOpen(BrowserFolderItem folderItem){
		//setDataContext(folderItem);
	}

	private synchronized void updateView(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ObservableList<IDeferLoaded<BrowserItemVM>> observableMedias;

				if(mediaModel != null && mediaModel.getMedias() != null){
					observableMedias = new ObservableListFXAdapter<>(mediaModel.getMedias());
				}else {
					observableMedias = FXCollections.observableArrayList();
					System.out.println("medias empty");
				}

				gridView.setItems(observableMedias);
				gridViewPort.ensureViewportChangedListener(); // TODO HACK: (Should be called once after Scene is shown)
			}
		});
	}

	public ISelectionManager<IBrowserItem> getSelectionManager() {
		return selectionManager;
	}

}
