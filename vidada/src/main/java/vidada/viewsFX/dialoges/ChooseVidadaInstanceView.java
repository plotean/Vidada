package vidada.viewsFX.dialoges;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.model.settings.VidadaInstance;

public class ChooseVidadaInstanceView extends BorderPane {

	ComboBox<VidadaInstance> cboDatabase = new ComboBox<VidadaInstance>();

	public ChooseVidadaInstanceView(Collection<VidadaInstance> availableInstances)
	{
		cboDatabase.setItems(FXCollections.observableArrayList(availableInstances));

		GridPane content = new GridPane();
		content.setHgap(10);
		content.setVgap(10);
		content.add(new Label("Vidada Instance:"), 0, 0);
		content.add(cboDatabase, 1, 0);
		GridPane.setHgrow(cboDatabase, Priority.ALWAYS);
		//this.add(new Label("Password"), 0, 1);
		//this.add(txPassword, 1, 1);
		//GridPane.setHgrow(txPassword, Priority.ALWAYS);

		if(availableInstances.size() > 0)
			cboDatabase.getSelectionModel().select(availableInstances.iterator().next());

		this.setCenter(content);
	}

	public VidadaInstance getDatabase(){
		return cboDatabase.getSelectionModel().getSelectedItem();
	}
}
