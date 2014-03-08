package vidada.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vidada.client.facade.VidadaClientFacade;

public class VidadaClientManager implements IVidadaClientManager {



	/*
	private static VidadaClientManager instance;

	public synchronized static VidadaClientManager instance(){
		if(instance == null){
			instance = new VidadaClientManager();
		}
		return instance;
	}*/

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	transient private final Map<String, IVidadaClientFacade> allClients = new HashMap<String, IVidadaClientFacade>();
	transient private IVidadaClientFacade active = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Singleton private constructor
	 */
	public VidadaClientManager(){ }

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**{@inheritDoc}*/
	@Override
	public void addClient(IVidadaClient client){
		IVidadaClientFacade clientFacade = new VidadaClientFacade(client);
		allClients.put(clientFacade.getInstanceId(), clientFacade);
		if(getActive() == null)
			setActive(clientFacade);
	}

	/**{@inheritDoc}*/
	@Override
	public List<IVidadaClientFacade> getAllClients(){
		return new ArrayList<IVidadaClientFacade>(allClients.values());
	}

	private void setActive(IVidadaClientFacade active){
		this.active = active;
	}

	/**{@inheritDoc}*/
	@Override
	public IVidadaClientFacade getActive() {
		return active;
	}

	/**
	 * Returns the local server (if any). 
	 * @return Will return null if there is no local server registered

	@Override
	public IVidadaServer getLocalServer(){
		return localServer;
	}


	public IMediaClientService getMediaClientService(){
		return mediaClientService;
	}

	public ITagClientService getTagClientService(){
		return tagClientService;
	}

	public IThumbnailClientService getThumbnailClientService(){
		return thumbnailClientService;
	}

	public IVidadaServer getOriginServer(MediaItem media) {
		IVidadaServer origin = allServers.get(media.getOriginId());
		if(origin == null){
			throw new NotSupportedException("Can not find origin of media "  + media + " media.origin name: " + media.getOriginId());
		}
		return origin;
	} */

}

