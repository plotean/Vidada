package vidada.client.facade;

import vidada.client.IVidadaClient;
import vidada.client.IVidadaClientFacade;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

/**
 * The vidada client facade aims to make usage of Vidada Client as easy as possible.
 * 
 * @author IsNull
 *
 */
public class VidadaClientFacade implements IVidadaClientFacade{

	private final IVidadaClient vidadaClient;
	private final IThumbContainerService thumbContainerService;

	/**
	 * Creates a new VidadaClientFacade
	 * @param vidadaClient
	 */
	public VidadaClientFacade(IVidadaClient vidadaClient){
		this.vidadaClient = vidadaClient;
		thumbContainerService = new ThumbContainerService(vidadaClient.getThumbnailClientService());
	}


	/**{@inheritDoc}*/
	@Override
	public IMediaClientService getMediaClientService() {
		return vidadaClient.getMediaClientService();
	}

	/**{@inheritDoc}*/
	@Override
	public ITagClientService getTagClientService() {
		return vidadaClient.getTagClientService();
	}

	/**{@inheritDoc}*/
	@Override
	public IThumbnailClientService getThumbnailClientService() {
		return vidadaClient.getThumbnailClientService();
	}

	/**{@inheritDoc}*/
	@Override
	public IThumbContainerService getThumbConatinerService(){
		return thumbContainerService;
	}

	/**{@inheritDoc}*/
	@Override
	public String getInstanceId() {
		return vidadaClient.getInstanceId();
	}
}
