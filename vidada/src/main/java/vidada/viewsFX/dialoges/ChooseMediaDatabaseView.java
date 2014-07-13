package vidada.viewsFX.dialoges;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.model.settings.VidadaDatabaseConfig;

public class ChooseMediaDatabaseView extends BorderPane {

	ComboBox<VidadaDatabaseConfig> cboDatabase = new ComboBox<VidadaDatabaseConfig>();

	public ChooseMediaDatabaseView(List<VidadaDatabaseConfig> availableDbs)
	{
		cboDatabase.setItems(FXCollections.observableArrayList(availableDbs));

		GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);
		content.add(new Label("Vidada Server Database:"), 0, 0);
		content.add(cboDatabase, 1, 0);
		GridPane.setHgrow(cboDatabase, Priority.ALWAYS);
		//this.add(new Label("Password"), 0, 1);
		//this.add(txPassword, 1, 1);
		//GridPane.setHgrow(txPassword, Priority.ALWAYS);

		if(availableDbs.size() > 0)
			cboDatabase.getSelectionModel().select(availableDbs.get(0));

		this.setCenter(content);
	}

	public VidadaDatabaseConfig getDatabase(){
		return cboDatabase.getSelectionModel().getSelectedItem();
	}
}
