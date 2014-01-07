package vidada.viewsFX.dialoges;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.model.settings.VidadaDatabase;

public class ChooseMediaDatabaseView extends BorderPane {

	ComboBox<VidadaDatabase> cboDatabase = new ComboBox<VidadaDatabase>();

	public ChooseMediaDatabaseView(List<VidadaDatabase> availableDbs)
	{
		cboDatabase.setItems(FXCollections.observableArrayList(availableDbs));

		GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);
		content.add(new Label("Media Database"), 0, 0);
		content.add(cboDatabase, 1, 0);
		GridPane.setHgrow(cboDatabase, Priority.ALWAYS);
		//this.add(new Label("Password"), 0, 1);
		//this.add(txPassword, 1, 1);
		//GridPane.setHgrow(txPassword, Priority.ALWAYS);
		this.setCenter(content);
	}

	public VidadaDatabase getDatabase(){
		return cboDatabase.getSelectionModel().getSelectedItem();
	}
}
