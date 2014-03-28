package vidada.viewsFX.dialoges;

import javafx.application.Platform;

import org.controlsfx.dialog.Dialog;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventListenerEx;

public class SynchronizeDialog extends Dialog {

	public SynchronizeDialog(Object owner) {
		super(owner, "Vidada Synchronisation");

		SyncMediaLibrariesView syncView = new SyncMediaLibrariesView();
		setContent(syncView);

		syncView.DoneEvent.add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						hide();
					}
				});
			}
		});

		if(syncView.isFinished()){
			hide();
		}
	}


}
