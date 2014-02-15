package vidada.viewsFX.dialoges;

import org.controlsfx.dialog.Dialog;

import vidada.viewsFX.settings.SettingsViewFX;

public final class SettingsDialog extends Dialog {

	public SettingsDialog(Object owner) {
		super(owner, "Settings");
		setMasthead("Vidada Settings");
		setContent(new SettingsViewFX());

		setResizable(true);
	}
}
