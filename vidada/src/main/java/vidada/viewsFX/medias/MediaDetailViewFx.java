package vidada.viewsFX.medias;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import vidada.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.tags.MediaDetailTagPane;

public class MediaDetailViewFx extends BorderPane {

	private final GridPane gridpane = new GridPane();
	private final MediaDetailTagPane mediaDetailTagPane;

	private final TextField txtname;


	private IMediaViewModel mediaVM;


	public MediaDetailViewFx(){

		mediaDetailTagPane = new MediaDetailTagPane();

		Label name = new Label("Name:");
		GridPane.setRowIndex(name, 1);
		GridPane.setColumnIndex(name, 1);
		gridpane.getChildren().add(name);

		txtname = new TextField("blubl");
		GridPane.setRowIndex(txtname, 1);
		GridPane.setColumnIndex(txtname, 2);
		gridpane.getChildren().add(txtname);


		this.setCenter(gridpane);
		this.setBottom(mediaDetailTagPane);
	}



	public void setDataContext(IMediaViewModel mediaVM){
		this.mediaVM = mediaVM;

		System.out.println("MediaDetailViewFx:DataContext := " + mediaVM);

		if(mediaVM != null){
			mediaDetailTagPane.setDataContext(mediaVM.getTagsVM());
		}else{
			System.err.println("MediaDetail: Setting Tags Model Context to NULL");
			mediaDetailTagPane.setDataContext(null);
		}
		updateModelToView();
	}

	private void updateModelToView(){
		if(mediaVM != null){
			txtname.setText(mediaVM.getFilename());
		}else{
			txtname.setText(null);
			txtname.setPromptText("Enter Name...");
		}
	}

}
