package vidada.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vidada.IVidadaServer;
import vidada.client.services.IMediaClientService;
import vidada.client.services.ITagClientService;
import vidada.model.media.MediaItem;
import archimedesJ.exceptions.NotImplementedException;

public class VidadaClientManager {

	transient private final Map<String, IVidadaServer> allServers = new HashMap<String, IVidadaServer>();


	public void addServer(IVidadaServer server){
		allServers.put(server.getNameId(), server);
	}

	public Collection<IVidadaServer> getAllServer(){
		return new ArrayList<IVidadaServer>(allServers.values());
	}

	public IMediaClientService getMediaClientService(){
		throw new NotImplementedException();
	}

	public ITagClientService getTagClientService(){
		throw new NotImplementedException();
	}

	public IVidadaServer getOriginServer(MediaItem media) {
		return allServers.get(media.getOriginId());
	}

}

