package vidada.model.media;

import vidada.model.libraries.MediaLibrary;
import archimedesJ.threading.IProgressListener;

public interface IMediaImportService {

	/**
	 * Scans all existing media libraries and updates the media files
	 * 
	 * @param progressListener Callback for progress status updates
	 */
	void scanAndUpdateDatabases(IProgressListener progressListener);


	/**
	 * Scans the given media library and updates the media files
	 * @param progressListener
	 * @param library
	 */
	void scanAndUpdateLibrary(IProgressListener progressListener, MediaLibrary library);

}