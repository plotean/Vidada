package vidada.client;

import vidada.client.services.IMediaClientService;
import vidada.client.services.IPingClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

/**
 * Represents a Vidada client, which offers all available services.
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

    /**
     * Returns the {@link IPingClientService} of this Vidada instance.
     * @return
     */
    IPingClientService getPingClientService();
}
