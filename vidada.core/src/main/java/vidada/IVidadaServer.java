package vidada;

import vidada.services.IMediaService;
import vidada.services.ITagService;

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
	 * 
	 * @return
	 */
	IMediaService getMediaService();

	/**
	 * Get the tag service for this server
	 * @return
	 */
	ITagService getTagService();

	/**
	 * Gets the unique name identifier of this server
	 * @return
	 */
	String getNameId();
}
