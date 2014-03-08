package vidada.client;

import vidada.client.facade.IThumbConatinerService;

/**
 * The Vidada client facade aims to make usage of Vidada Client as easy as possible.
 * It basically extends the default {@link IVidadaClient} with some additional helpful services.
 * 
 * @author IsNull
 *
 */
public interface IVidadaClientFacade extends IVidadaClient {

	/**
	 * Gets the IThumbConatinerService which provides an easy way to retrieve thumbs
	 * @return
	 */
	public abstract IThumbConatinerService getThumbConatinerService();
}