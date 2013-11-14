package vidada.viewsFX.medias;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import vidada.views.media.IMediaViewModel;
import vidada.viewsFX.tags.MediaDetailTagPane;

public class MediaDetailViewFx extends BorderPane {

	private final GridPane gridpane = new GridPane();
	private final MediaDetailTagPane mediaDetailTagPane;


	private IMediaViewModel mediaVM;


	public MediaDetailViewFx(){

		mediaDetailTagPane = new MediaDetailTagPane();



		Label name = new Label("Name:");
		GridPane.setRowIndex(name, 1);
		GridPane.setColumnIndex(name, 1);
		gridpane.getChildren().add(name);

		TextField txtname = new TextField("blubl");
		GridPane.setRowIndex(txtname, 1);
		GridPane.setColumnIndex(txtname, 2);
		gridpane.getChildren().add(txtname);



		this.setCenter(gridpane);
		this.setBottom(mediaDetailTagPane);
	}



	public void setDataContext(IMediaViewModel mediaVM){
		this.mediaVM = mediaVM;
		mediaDetailTagPane.setDataContext(null); // TODO !
	}

}
