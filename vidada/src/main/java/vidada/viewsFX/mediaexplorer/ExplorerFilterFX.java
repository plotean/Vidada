package vidada.viewsFX.mediaexplorer;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import vidada.model.libraries.MediaLibrary;

public class ExplorerFilterFX extends BorderPane {

	private final ComboBox<MediaLibrary> cboMediaLibrary= new ComboBox<>();
	private final Label libraryDescription = new Label("Media Library:");

	public ExplorerFilterFX(){

		this.setPadding(new Insets(10));

		Insets margrin = new Insets(5,5,10,0);
		HBox.setMargin(libraryDescription, margrin);
		HBox.setMargin(cboMediaLibrary, margrin);

		HBox box = new HBox();
		box.getChildren().add(libraryDescription);
		box.getChildren().add(cboMediaLibrary);


		setCenter(box);
	}
}
