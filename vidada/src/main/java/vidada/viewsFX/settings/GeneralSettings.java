package vidada.viewsFX.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vidada.model.settings.VidadaClientSettings;

public class GeneralSettings extends BorderPane {

	private final VidadaClientSettings applicationSettings = VidadaClientSettings.instance();

	private final CheckBox enableDirectPlaySound;

	public GeneralSettings(){

		VBox simpleSettings = new VBox();

		enableDirectPlaySound = new CheckBox("DirectPlay Sound Enabled");
		enableDirectPlaySound.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs,
					Boolean oldVal, Boolean newVal) {
				applicationSettings.setEnableDirectPlaySound(newVal);
				applicationSettings.persist();
			}
		});
		simpleSettings.getChildren().addAll(enableDirectPlaySound);


		BorderPane.setMargin(simpleSettings, new Insets(10));
		this.setCenter(simpleSettings);

		updateView();
	}



	private void updateView(){
		enableDirectPlaySound.setSelected(applicationSettings.isEnableDirectPlaySound());
	}
}
