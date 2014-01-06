package vidada.viewsFX.mediabrowsers;


import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.model.browser.BrowserFolderItem;
import vidada.model.browser.BrowserMediaItem;
import vidada.model.browser.IBrowserItem;
import vidada.model.browser.IDataProvider;
import vidada.viewmodel.IVMFactory;
import vidada.viewmodel.MediaViewModel;
import vidada.viewmodel.ViewModelPool;
import vidada.viewmodel.browser.BrowserFolderItemVM;
import vidada.viewmodel.browser.BrowserItemVM;
import vidada.viewsFX.mediabrowsers.VirtualListAdapter.ITransform;
import vidada.viewsFX.player.IMediaPlayerService;
import vlcj.fx.MediaPlayerService;
import archimedesJ.data.events.CollectionEventArg;
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

	private IDataProvider<IBrowserItem> mediaModel;
	private GridView<BrowserItemVM> gridView;
	private GridViewViewPort gridViewPort;
	private IndexRange visibleCells;



	//transient private final ObservableList<BrowserItemVM> observableMedias;
	transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


	public MediaBrowserFX(){
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


		ObservableList<BrowserItemVM> observableMedias = gridView.getItems();

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

	public void setDataContext(IDataProvider<IBrowserItem> mediaModel){

		System.out.println("MediaBrowserFX: setDataContext" + mediaModel);


		selectionManager.clear();

		if(this.mediaModel != null){
			this.mediaModel.getItemsChangedEvent().remove(mediasChangedEventListener);
		}

		this.mediaModel = mediaModel;

		if(mediaModel != null){
			mediaModel.getItemsChangedEvent().add(mediasChangedEventListener);
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

	/**
	 * Occurs when a folderItem was opened
	 * @param folderItem
	 */
	protected void onItemFolderOpen(BrowserFolderItem folderItem){
		//setDataContext(folderItem);
	}

	transient private final ViewModelPool<IBrowserItem, BrowserItemVM> mediaVMPool =
			new ViewModelPool<IBrowserItem, BrowserItemVM>(vmFactory);


	private synchronized void updateView(){
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(mediaModel != null){

					System.out.println("MediaBrowserFX:updateView items: " + mediaModel.size() + " empty?" + mediaModel.isEmpty() + " :: " + mediaModel);

					for (int i = 0; i <  mediaModel.size() ; i++) {
						System.out.println();
					}

					VirtualListAdapter<BrowserItemVM, IBrowserItem> listAdapter =
							new VirtualListAdapter<>(mediaModel, new ITransform<BrowserItemVM, IBrowserItem>() {
								@Override
								public BrowserItemVM transform(IBrowserItem source) {
									return mediaVMPool.viewModel(source); //vmFactory.create(source); //
								}
							});

							gridView.setItems(listAdapter);
				}else {
					System.out.println("medias empty");
				}
			}
		});
	}

	public ISelectionManager<IBrowserItem> getSelectionManager() {
		return selectionManager;
	}

}
