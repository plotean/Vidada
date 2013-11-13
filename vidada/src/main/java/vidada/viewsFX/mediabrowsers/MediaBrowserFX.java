package vidada.viewsFX.mediabrowsers;


import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.model.browser.MediaBrowserModel;
import vidada.model.media.MediaItem;
import vidada.viewsFX.player.IMediaPlayerService;
import vlcj.fx.MediaPlayerService;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventListenerEx;

/**
 * Represents the media browser
 * @author IsNull
 *
 */
public class MediaBrowserFX extends BorderPane {

	private MediaBrowserModel mediaModel;
	private GridView<MediaItem> gridView;

	transient private final ObservableList<MediaItem> observableMedias;
	transient private final IMediaPlayerService mediaPlayerService = new MediaPlayerService();


	public MediaBrowserFX(){
		observableMedias = FXCollections.observableArrayList(new ArrayList<MediaItem>());
		initView();		
	}

	private void initView(){
		gridView = new GridView<MediaItem>();

		gridView.setStyle("-fx-background-color: #FFFFFF;");

		gridView.getStylesheets().add("css/default_media_cell.css");

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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				observableMedias.clear();

				if(mediaModel != null){

					final List<MediaItem> items = mediaModel.getRaw();
					System.out.println("MediaBrowserFX:updateView items: " + items.size() + " empty?" + items.isEmpty());

					if(!items.isEmpty()){
						observableMedias.addAll(items);
					}

				}else {
					System.out.println("medias empty");
				}
			}
		});
	}

	class MediaGridItemCell extends GridCell<MediaItem> {

		private final MediaItemView visual; 

		public MediaGridItemCell(){
			visual = new MediaItemView(mediaPlayerService);

			DoubleProperty desiredWidth = gridView.cellWidthProperty();
			DoubleProperty desiredHeight = gridView.cellHeightProperty();

			this.prefWidthProperty().bind(desiredWidth);
			this.prefHeightProperty().bind(desiredHeight);
			this.maxWidthProperty().bind(desiredWidth);
			this.maxHeightProperty().bind(desiredHeight);
			this.minWidthProperty().bind(desiredWidth);
			this.minHeightProperty().bind(desiredHeight);

			setGraphic(visual);
		}

		@Override
		public void updateItem(MediaItem item, boolean empty) {
			super.updateItem(item, empty);
			visual.setDataContext(item);
		}
	}
}
