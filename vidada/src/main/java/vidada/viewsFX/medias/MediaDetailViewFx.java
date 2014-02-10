package vidada.viewsFX.medias;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import org.controlsfx.control.Rating;

import vidada.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.tags.MediaDetailTagPane;

public class MediaDetailViewFx extends BorderPane {

	private final GridPane gridpane = new GridPane();
	private final MediaDetailTagPane mediaDetailTagPane;

	private final TextField txtname;
	private final Label lblResolution;
	private final Rating rating;


	private IMediaViewModel mediaVM;


	public MediaDetailViewFx(){

		mediaDetailTagPane = new MediaDetailTagPane();

		//gridpane.setPadding(new Insets(10));

		Label name = new Label("Name:");
		GridPane.setConstraints(name, 1, 1);
		gridpane.getChildren().add(name);

		txtname = new TextField("blubl");
		GridPane.setConstraints(txtname, 2, 1);
		GridPane.setHgrow(txtname, Priority.SOMETIMES);
		GridPane.setMargin(txtname, new Insets(0,0,0,10));
		gridpane.getChildren().add(txtname);


		rating = new Rating();
		rating.setScaleX(0.5);
		rating.setScaleY(0.5);

		GridPane.setConstraints(rating, 3, 1);
		GridPane.setMargin(rating, new Insets(0,0,0,10));
		gridpane.getChildren().add(rating);


		// Second Row:

		lblResolution = new Label("Resolution");
		GridPane.setConstraints(lblResolution, 1, 2);
		gridpane.getChildren().add(lblResolution);


		this.setCenter(gridpane);
		BorderPane.setMargin(gridpane, new Insets(0,0,10,0));
		this.setBottom(mediaDetailTagPane);


		rating.ratingProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				mediaVM.setRating((int)rating.getRating());
			}
		});
	}



	public void setDataContext(IMediaViewModel mediaVM){
		this.mediaVM = mediaVM;

		System.out.println("MediaDetailViewFx:DataContext := " + mediaVM);

		if(mediaVM != null){
			mediaDetailTagPane.setDataContext(mediaVM);
		}else{
			System.err.println("MediaDetail: Setting Tags Model Context to NULL");
			mediaDetailTagPane.setDataContext(null);
		}
		updateModelToView();
	}

	private void updateModelToView(){
		if(mediaVM != null){
			txtname.setText(mediaVM.getFilename());
			lblResolution.setText(mediaVM.getResolution());
			rating.setRating(mediaVM.getRating());
		}else{
			txtname.setText(null);
			txtname.setPromptText("Enter Name...");
		}
	}

}
