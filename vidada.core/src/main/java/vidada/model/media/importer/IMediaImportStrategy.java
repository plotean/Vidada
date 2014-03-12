package vidada.model.media.importer;

import vidada.model.media.MediaLibrary;
import archimedesJ.threading.IProgressListener;


public interface IMediaImportStrategy {

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