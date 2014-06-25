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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.IVidadaServer;

public class SyncMediaLibrariesView extends GridPane {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(SyncMediaLibrariesView.class.getName());

    private final ProgressBar bar = new ProgressBar();
	private final Label currentActivity = new Label("Ready.");

	public final EventHandlerEx<EventArgs> DoneEvent = new EventHandlerEx<EventArgs>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/


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

        logger.info("Syncing media libraries start...");

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
                            logger.info("SyncMediaLibrariesView: synchronizing media libraries done.");
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
                logger.warn("SyncMediaLibrariesView: LocalServer not available");
            }
        }catch(Exception e){
            logger.error(e);
        }

	}

}
