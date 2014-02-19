package vidada.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vidada.IVidadaServer;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.client.services.IThumbnailClientService;
import vidada.client.services.MediaClientService;
import vidada.client.services.TagClientService;
import vidada.client.services.ThumbnailClientService;
import vidada.model.media.MediaItem;
import archimedesJ.exceptions.NotSupportedException;

public class VidadaClientManager {

	/***************************************************************************
	 *                                                                         *
	 * Singleton implementation                                                *
	 *                                                                         *
	 **************************************************************************/


	private static VidadaClientManager instance;

	public synchronized static VidadaClientManager instance(){
		if(instance == null){
			instance = new VidadaClientManager();
		}
		return instance;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	transient private final Map<String, IVidadaServer> allServers = new HashMap<String, IVidadaServer>();

	private final IMediaClientService mediaClientService;
	private final ITagClientService tagClientService;
	private final IThumbnailClientService thumbnailClientService;

	private IVidadaServer localServer;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Singleton private constructor
	 */
	private VidadaClientManager(){
		mediaClientService = new MediaClientService(this);
		tagClientService = new TagClientService(this);
		thumbnailClientService = new ThumbnailClientService(this);
	}

	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Add the given server
	 * @param server
	 */
	public void addServer(IVidadaServer server){
		allServers.put(server.getNameId(), server);
		if(server.isLocal())
			localServer = server;
	}

	/**
	 * Returns all servers currently known to the client manager
	 * @return
	 */
	public Collection<IVidadaServer> getAllServer(){
		return new ArrayList<IVidadaServer>(allServers.values());
	}

	/**
	 * Returns the local server (if any). 
	 * @return Will return null if there is no local server registered
	 */
	public IVidadaServer getLocalServer(){
		return localServer;
	}


	/***************************************************************************
	 *                                                                         *
	 * Exposed Services                                                        *
	 *                                                                         *
	 **************************************************************************/

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
	}

}

