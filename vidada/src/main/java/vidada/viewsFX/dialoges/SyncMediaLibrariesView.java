package vidada.viewsFX.dialoges;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.threading.IProgressListener;
import archimedes.core.threading.ProgressEventArgs;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.IVidadaServer;

public class SyncMediaLibrariesView extends GridPane {

	private final ProgressBar bar = new ProgressBar();
	private final Label currentActivity = new Label("Ready.");

	public final EventHandlerEx<EventArgs> DoneEvent = new EventHandlerEx<EventArgs>();


	public SyncMediaLibrariesView(){

		Insets inset = new Insets(10);

		this.setHgap(inset.getRight());
		this.setVgap(inset.getTop());

		this.add(bar, 0, 0);
		GridPane.setHgrow(bar, Priority.ALWAYS);
		bar.prefWidthProperty().bind(this.widthProperty().subtract(inset.getRight()));
		this.add(currentActivity, 0, 1);
		GridPane.setHgrow(currentActivity, Priority.ALWAYS);

		BorderPane.setMargin(bar, inset);
		BorderPane.setMargin(currentActivity, inset);


		startSync();
	}

	boolean isFinished = false;
	public boolean isFinished(){
		return isFinished;
	}

	private synchronized void startSync(){

		System.out.println("SyncMediaLibrariesView: startSync");

		final IProgressListener listener = new IProgressListener() {
			@Override
			public void currentProgress(final ProgressEventArgs progressInfo) {

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						bar.setProgress(progressInfo.getProgressInPercent() / 100.0);
						currentActivity.setText(progressInfo.getCurrentTask());

                        if(progressInfo.isCompleted()){
                            isFinished = true;
                            System.out.println("SyncMediaLibrariesView: synchronizing media libraries done.");
                            DoneEvent.fireEvent(SyncMediaLibrariesView.this, EventArgs.Empty);
                        }
					}
				});
			}
		};

        try{
            IVidadaServer localServer = vidada.Application.getLocalServer();
            if(localServer != null){
                localServer.getImportService().synchronizeAll(listener);
            }else {
                System.err.println("SyncMediaLibrariesView: LocalServer not available");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

	}

}
