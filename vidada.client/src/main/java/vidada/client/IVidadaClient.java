package vidada.client;

import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

/**
 * Represents Vidada a client, and holds all available services of it.
 * @author IsNull
 *
 */
public interface IVidadaClient {

	/**
	 * Gets the unique name of the connected Vidada server
	 * @return
	 */
	String getInstanceId();

	/**
	 * Returns the {@link IMediaClientService} of this Vidada instance.
	 * @return
	 */
	IMediaClientService getMediaClientService();

	/**
	 * Returns the {@link ITagClientService} of this Vidada instance.
	 * @return
	 */
	ITagClientService getTagClientService();

	/**
	 * Returns the {@link IThumbnailClientService} of this Vidada instance.
	 * @return
	 */
	IThumbnailClientService getThumbnailClientService();
}
