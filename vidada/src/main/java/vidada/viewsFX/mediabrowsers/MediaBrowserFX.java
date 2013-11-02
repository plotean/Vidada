package vidada.viewsFX.mediabrowsers;


import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.model.browser.MediaBrowserModel;
import vidada.model.media.MediaItem;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventListenerEx;


public class MediaBrowserFX extends BorderPane {

	private MediaBrowserModel mediaModel;

	private GridView<MediaItem> gridView;
	transient private final ObservableList<MediaItem> observableMedias;


	public MediaBrowserFX(){
		observableMedias = FXCollections.observableArrayList(new ArrayList<MediaItem>());
		initView();		
	}

	private void initView(){
		gridView = new GridView<MediaItem>();

		gridView.setStyle("-fx-background-color: #FFFFFF;");

		gridView.setCellFactory(new Callback<GridView<MediaItem>, GridCell<MediaItem>>() {
			@Override
			public GridCell<MediaItem> call(GridView<MediaItem> arg0) {
				//createdCellNodes++;
				//System.out.println("createdCellNodes: " + createdCellNodes);
				return new MediaGridItemCell();
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

	public void setDataContext(MediaBrowserModel mediaModel){

		if(this.mediaModel != null)
			this.mediaModel.getMediasChangedEvent().remove(mediasChangedEventListener);

		this.mediaModel = mediaModel;
		this.mediaModel.getMediasChangedEvent().add(mediasChangedEventListener);

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

	private synchronized void updateView(){

		System.out.println("MediaBrowserFX:updateView!");

		observableMedias.clear();

		if(mediaModel != null){
			System.out.println("MediaBrowserFX:updateView items: " + mediaModel.size());

			if(!mediaModel.isEmpty()){
				observableMedias.addAll(mediaModel.getRaw());
			}
		}
	}

	static class MediaGridItemCell extends GridCell<MediaItem> {

		private final MediaItemView visual = new MediaItemView();

		public MediaGridItemCell(){
			setGraphic(visual);
		}

		@Override
		public void updateItem(MediaItem item, boolean empty) {
			super.updateItem(item, empty);
			visual.setDataContext(item);
		}
	}
}
