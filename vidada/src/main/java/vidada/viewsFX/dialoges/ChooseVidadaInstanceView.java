package vidada.viewsFX.dialoges;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.model.settings.VidadaInstance;

import java.util.Collection;

public class ChooseVidadaInstanceView extends BorderPane {

    private final ListView<VidadaInstance> databasesView = new ListView<>();

    public ChooseVidadaInstanceView(Collection<VidadaInstance> availableInstances)
	{
        databasesView.setItems(FXCollections.observableArrayList(availableInstances));

		GridPane content = new GridPane();

		content.setHgap(10);
		content.setVgap(10);
        Label label =  new Label("Vidada Instance:");
		content.add(label, 0, 0);
		content.add(databasesView, 1, 0);

        GridPane.setValignment(label, VPos.TOP);
		GridPane.setHgrow(databasesView, Priority.ALWAYS);
		//this.add(new Label("Password"), 0, 1);
		//this.add(txPassword, 1, 1);
		//GridPane.setHgrow(txPassword, Priority.ALWAYS);

		if(availableInstances.size() > 0)
            databasesView.getSelectionModel().select(availableInstances.iterator().next());


        BorderPane.setMargin(content, new Insets(10));
		this.setCenter(content);
	}

	public VidadaInstance getDatabase(){
		return databasesView.getSelectionModel().getSelectedItem();
	}
}
