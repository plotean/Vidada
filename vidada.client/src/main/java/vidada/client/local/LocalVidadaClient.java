package vidada.client.local;

import vidada.IVidadaServer;
import vidada.client.IVidadaClient;
import vidada.client.local.impl.LocalMediaClientService;
import vidada.client.local.impl.LocalTagClientService;
import vidada.client.local.impl.LocalThumbnailClientService;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;

/**
 * 
 * @author IsNull
 *
 */
public class LocalVidadaClient implements IVidadaClient {

	private final IVidadaServer localServer;

	private final IMediaClientService mediaClientService;
	private final ITagClientService tagClientService;
	private final IThumbnailClientService thumbnailClientService;

	/**
	 * Creates a local Vidada Client using the given local Vidada server
	 * @param server
	 */
	public LocalVidadaClient(IVidadaServer server){
		this.localServer = server;
		mediaClientService = new LocalMediaClientService(server.getMediaService());
		tagClientService = new LocalTagClientService(server.getTagService());
		thumbnailClientService = new LocalThumbnailClientService(server.getThumbnailService());
	}

	@Override
	public IMediaClientService getMediaClientService() {
		return mediaClientService;
	}

	@Override
	public ITagClientService getTagClientService() {
		return tagClientService;
	}

	@Override
	public IThumbnailClientService getThumbnailClientService() {
		return thumbnailClientService;
	}

	@Override
	public String getInstanceId() {
		return localServer.getNameId();
	}
}
