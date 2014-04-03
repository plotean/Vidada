package vidada.model.media.importer;

import archimedes.core.threading.IProgressListener;


public interface IMediaImportStrategy {

	/**
	 * Scans all existing media libraries and synchronizes/updates the media files.
     *
     * - Updates existing medias
     * - Remove sources no longer available
     * - Add new media items
	 * 
	 * @param progressListener Callback for progress status updates
	 */
	void synchronize(IProgressListener progressListener);
}