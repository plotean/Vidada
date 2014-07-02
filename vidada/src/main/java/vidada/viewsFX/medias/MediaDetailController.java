package vidada.viewsFX.medias;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.controlsfx.control.Rating;
import vidada.client.viewmodel.media.IMediaViewModel;
import vidada.viewsFX.tags.MediaDetailTagPane;

import java.net.URL;
import java.util.ResourceBundle;


public class MediaDetailController implements Initializable {

    private static final Logger logger = LogManager.getLogger(MediaDetailController.class.getName());

    @FXML
	private MediaDetailTagPane mediaDetailTagPane;

    @FXML
	private TextField txtname;
    @FXML
    private Rating ratingView;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblSize;
    @FXML
	private Label lblResolution;


    private final DoubleProperty rating = new SimpleDoubleProperty();
    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty creationDate = new SimpleStringProperty();
    private final StringProperty fileSize = new SimpleStringProperty();

	private IMediaViewModel mediaVM;

    public MediaDetailController(){

        setDataContext(null);
        fileName.setValue("huhu");


        rating.addListener((observableValue, oldValue, newValue) -> {
            if(mediaVM != null){
                mediaVM.setRating((int)rating.get());
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtname.textProperty().bindBidirectional(fileName);
        ratingView.ratingProperty().bindBidirectional(rating);
        lblDate.textProperty().bindBidirectional(creationDate);
        lblSize.textProperty().bindBidirectional(fileSize);
    }




	public void setDataContext(IMediaViewModel mediaVM){
		this.mediaVM = mediaVM;

        logger.debug("DataContext := " + mediaVM);

        if(mediaDetailTagPane != null) {
            if (mediaVM != null) {
                mediaDetailTagPane.setDisable(false);
                mediaDetailTagPane.setDataContext(mediaVM);
            } else {
                mediaDetailTagPane.setDataContext(null);
                mediaDetailTagPane.setDisable(true);
            }
        }
		updateModelToView();
	}

	private void updateModelToView(){
		if(mediaVM != null){
			//this.setDisable(false);
            fileName.set(mediaVM.getFilename());
            rating.set(mediaVM.getRating());
            creationDate.set(mediaVM.getAddedDate());
            fileSize.set(mediaVM.getFileSizeStr());

			//lblResolution.setText("Resolution: " + mediaVM.getResolution());
            //lblDate.setText("Date: " + mediaVM.getAddedDate());
			//rating.setRating(mediaVM.getRating());
		}else{
			//this.setDisable(true);
		}
	}


}
