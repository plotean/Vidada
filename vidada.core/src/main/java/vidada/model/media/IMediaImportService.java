package vidada.model.media;

import archimedesJ.threading.IProgressListener;

public interface IMediaImportService {

	/* (non-Javadoc)
	 * @see vidada.model.items.IMediaService#scanAndUpdateDatabases(archimedesJ.threading.IProgressListener)
	 */
	public abstract void scanAndUpdateDatabases(IProgressListener progressListener);

}