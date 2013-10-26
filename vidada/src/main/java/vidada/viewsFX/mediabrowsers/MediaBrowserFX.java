package vidada.viewsFX.mediabrowsers;


import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import vidada.model.media.MediaItem;


public class MediaBrowserFX extends BorderPane {

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

	public void setDataContext(List<MediaItem> items){
		System.out.println("items: " + items.size());

		observableMedias.clear();
		if(items != null && !items.isEmpty()){
			observableMedias.addAll(items);
		}
	}

	static class MediaGridItemCell extends GridCell<MediaItem> {

		private final MediaItemPanel visual = new MediaItemPanel();

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
