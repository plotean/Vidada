package vidada;

import vidada.services.IJobService;
import vidada.services.IMediaImportService;
import vidada.services.IMediaLibraryService;
import vidada.services.IMediaService;
import vidada.services.ITagService;
import vidada.services.IThumbnailService;

/**
 * Represents a Vidada server
 * 
 * @author IsNull
 *
 */
public interface IVidadaServer {

	/**
	 * Is this server local?
	 * @return
	 */
	boolean isLocal();

	/**
	 * Get the library service for this server
	 * @return
	 */
	IMediaLibraryService getLibraryService();

	/**
	 * Get the media service for this server
	 * @return
	 */
	IMediaService getMediaService();

	/**
	 * Get the tag service for this server
	 * @return
	 */
	ITagService getTagService();

	/**
	 * 
	 * @return
	 */
	IThumbnailService getThumbnailService();

	/**
	 * 
	 * @return
	 */
	IMediaImportService getImportService();

	/**
	 * 
	 * @return
	 */
	IJobService getJobService();

	/**
	 * Gets the unique name identifier of this server
	 * @return
	 */
	String getNameId();
}
