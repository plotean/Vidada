package vidada.viewsFX.dialoges;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import vidada.IVidadaServer;
import vidada.model.media.store.local.IMediaImportStrategy;
import vidada.model.media.store.local.MediaImportStrategy;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;

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

	private Task<Void> task;

	private synchronized void startSync(){

		System.out.println("SyncMediaLibrariesView: startSync");

		final IProgressListener listener = new IProgressListener() {
			@Override
			public void currentProgress(final ProgressEventArgs progressInfo) {

				System.out.println("SyncMediaLibrariesView::Progress: " + progressInfo.getCurrentTask());

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						bar.setProgress(progressInfo.getProgressInPercent() / 100.0);
						currentActivity.setText(progressInfo.getCurrentTask());
					}
				});
			}
		};

		task = new Task<Void>() {
			@Override public Void call() {
				System.out.println("SyncMediaLibrariesView: synchronizing media libraries...");

				try{
					IVidadaServer localServer = null; // TODO

					IMediaImportStrategy mediaImporter = new MediaImportStrategy(localServer.getMediaService(), localServer.getTagService(), localServer.getLibraryService());
					mediaImporter.scanAndUpdateDatabases(listener);
				}catch(Exception e){
					e.printStackTrace();

				}

				isFinished = true;
				DoneEvent.fireEvent(SyncMediaLibrariesView.this, EventArgs.Empty);
				System.out.println("SyncMediaLibrariesView: synchronizing media libraries done.");
				return null;
			}
		};
		t = new Thread(task);
		t.start();
	}

	Thread t;
}
