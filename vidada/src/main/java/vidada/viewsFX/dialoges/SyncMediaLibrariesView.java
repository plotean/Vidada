package vidada.viewsFX.dialoges;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import vidada.model.ServiceProvider;
import vidada.model.media.IMediaImportService;
import vidada.model.media.IMediaService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;

public class SyncMediaLibrariesView extends BorderPane {

	private final ProgressBar bar = new ProgressBar();
	private final Label currentActivity = new Label("Ready.");

	public final EventHandlerEx<EventArgs> DoneEvent = new EventHandlerEx<EventArgs>();


	public SyncMediaLibrariesView(){

		BorderPane.setAlignment(bar, Pos.CENTER);

		HBox progressBox = new HBox();
		progressBox.getChildren().add(bar);
		HBox.setHgrow(bar, Priority.ALWAYS);

		BorderPane.setMargin(progressBox, new Insets(10));
		BorderPane.setMargin(currentActivity, new Insets(10));

		setCenter(progressBox);
		setBottom(currentActivity);

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
					IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
					IMediaImportService mediaImporter = mediaService.getMediaImporter();
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
