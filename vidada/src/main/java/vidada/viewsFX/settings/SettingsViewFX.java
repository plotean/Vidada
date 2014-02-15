package vidada.viewsFX.settings;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class SettingsViewFX extends BorderPane {

	private final TabPane tabPane;

	public SettingsViewFX(){

		tabPane = new TabPane();

		tabPane.setBackground(null);

		addSettingsTab("General", new GeneralSettings());

		addSettingsTab("Privacy", new Label("TODO-Privacy"));

		addSettingsTab("Server", new Label("TODO-Server"));

		addSettingsTab("About", new AboutPane());

		this.setCenter(tabPane);
	}

	private void addSettingsTab(String name, Node node){
		Tab tab = new Tab();
		tab.setClosable(false);
		tab.setText(name);
		tab.setContent(node);
		tabPane.getTabs().add(tab);
	}
}
