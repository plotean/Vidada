package vidada.viewsFX.medias;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.Rating;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.tags.MediaDetailTagPane;

public class MediaDetailViewFx extends BorderPane {

    private static final Logger logger = LogManager.getLogger(MediaDetailViewFx.class.getName());


    private final GridPane gridpane = new GridPane();
	private final MediaDetailTagPane mediaDetailTagPane;

	private final TextField txtname;
	private final Label lblResolution;
    private final Label lblDate;
	private final Rating rating;


	private IMediaViewModel mediaVM;


	public MediaDetailViewFx(){

		mediaDetailTagPane = new MediaDetailTagPane();

		//gridpane.setPadding(new Insets(10));
        gridpane.setHgap(10);

		Label name = new Label("Name:");
		GridPane.setConstraints(name, 1, 1);
		gridpane.getChildren().add(name);

		txtname = new TextField();
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

		lblResolution = new Label("Resolution:");
		GridPane.setConstraints(lblResolution, 1, 2);
		gridpane.getChildren().add(lblResolution);

        lblDate = new Label("Date:");
        GridPane.setConstraints(lblDate, 2, 2);
        gridpane.getChildren().add(lblDate);


		this.setCenter(gridpane);
		BorderPane.setMargin(gridpane, new Insets(0,0,10,0));
		this.setBottom(mediaDetailTagPane);


		rating.ratingProperty().addListener((observableValue, oldValue, newValue) -> {
            mediaVM.setRating((int)rating.getRating());
        });

		setDataContext(null);
	}



	public void setDataContext(IMediaViewModel mediaVM){
		this.mediaVM = mediaVM;

        logger.debug("DataContext := " + mediaVM);

		if(mediaVM != null){
			mediaDetailTagPane.setDisable(false);
			mediaDetailTagPane.setDataContext(mediaVM);
		}else{
			mediaDetailTagPane.setDataContext(null);
			mediaDetailTagPane.setDisable(true);
		}
		updateModelToView();
	}

	private void updateModelToView(){
		if(mediaVM != null){
			this.setDisable(false);
			txtname.setText(mediaVM.getFilename());
			lblResolution.setText("Resolution: " + mediaVM.getResolution());
            lblDate.setText("Date: " + mediaVM.getAddedDate());
			rating.setRating(mediaVM.getRating());
		}else{
			this.setDisable(true);
		}
	}

}
