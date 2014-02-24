package vidada.client.services;

import java.util.List;

import vidada.IVidadaServer;

public interface IVidadaServerClientService {

	/**
	 * Add the given server
	 * @param server
	 */
	void addServer(IVidadaServer server);

	/**
	 * Returns all servers currently known to the client manager
	 * @return
	 */
	List<IVidadaServer> getAllServer();

	/**
	 * Returns the local server (if any). 
	 * @return Will return null if there is no local server registered
	 */
	public IVidadaServer getLocalServer();
}
