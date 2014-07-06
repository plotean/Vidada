package vidada;

import vidada.server.services.*;

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
	 * Get the thumbnail service for this server
	 * @return
	 */
	IThumbnailService getThumbnailService();

	/**
	 * Get the import service for this server
	 * @return
	 */
	IMediaImportService getImportService();

	/**
	 * Get the job service for this server
	 * @return
	 */
	IJobService getJobService();

    /**
     * Get the user service for this server
     * @return
     */
    IUserService getUserService();

	/**
	 * Gets the unique name identifier of this server
	 * @return
	 */
	String getNameId();
}
