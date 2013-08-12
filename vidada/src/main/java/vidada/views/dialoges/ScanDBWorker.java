package vidada.views.dialoges;

import javax.swing.SwingWorker;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaImportService;
import vidada.model.media.IMediaService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.threading.IProgressListener;

public class ScanDBWorker extends SwingWorker<Boolean, Object>  {

	private final IProgressListener progressListener;

	public final EventHandlerEx<EventArgs> DoneEvent = new EventHandlerEx<EventArgs>();

	public ScanDBWorker(IProgressListener progressListener){
		this.progressListener = progressListener;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);
			IMediaImportService mediaImporter = mediaService.getMediaImporter();

			mediaImporter.scanAndUpdateDatabases(this.progressListener);
			setProgress(getProgress());
		} catch (Exception e) {
			exception = e;
			e.printStackTrace();
		}finally{
			DoneEvent.fireEvent(this, EventArgs.Empty);
		}
		return true;
	}

	private Exception exception;

	public Exception getExceptionDetail(){
		return exception;
	}

	public boolean success() {
		return exception == null;
	}

}
