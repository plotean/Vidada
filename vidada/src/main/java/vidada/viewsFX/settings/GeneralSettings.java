package vidada.viewsFX.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vidada.model.settings.DataBaseSettingsManager;
import vidada.model.settings.DatabaseSettings;

public class GeneralSettings extends BorderPane {

	private final DatabaseSettings applicationSettings = DataBaseSettingsManager.getSettings();

	private final CheckBox enableDirectPlaySound;

	public GeneralSettings(){

		VBox simpleSettings = new VBox();

		enableDirectPlaySound = new CheckBox("DirectPlay Sound Enabled");
		enableDirectPlaySound.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs,
					Boolean oldVal, Boolean newVal) {
				applicationSettings.setPlaySoundDirectPlay(newVal);
				DataBaseSettingsManager.persist(applicationSettings);
			}
		});
		simpleSettings.getChildren().addAll(enableDirectPlaySound);


		BorderPane.setMargin(simpleSettings, new Insets(10));
		this.setCenter(simpleSettings);

		updateView();
	}



	private void updateView(){
		enableDirectPlaySound.setSelected(applicationSettings.isPlaySoundDirectPlay());
	}
}
