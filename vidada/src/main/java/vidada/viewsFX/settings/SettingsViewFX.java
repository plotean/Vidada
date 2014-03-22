package vidada.viewsFX.settings;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SettingsViewFX extends BorderPane {

	private final TabPane tabPane;

	public SettingsViewFX(){

		tabPane = new TabPane();

		tabPane.setBackground(null);

        Pane myPane = null;

        try {
            myPane = (Pane) FXMLLoader.load(getClass().getResource("./../libraries/LibraryManagerView.fxml"));

        } catch (IOException e) {
            e.printStackTrace();
        }



		addSettingsTab("General", new GeneralSettings());

		addSettingsTab("Privacy", new Label("TODO-Privacy"));

		addSettingsTab("Server", new ServerSettings());

		addSettingsTab("About", new AboutPane());

        addSettingsTab("Test", myPane);

		this.setCenter(tabPane);
	}

	private void addSettingsTab(String name, Node node){
		Tab tab = new Tab();
		tab.setClosable(false);
		tab.setText(name);

		BorderPane border = new BorderPane();
		BorderPane.setMargin(node, new Insets(20));
		border.setCenter(node);
		tab.setContent(border);

		tabPane.getTabs().add(tab);
	}
}
