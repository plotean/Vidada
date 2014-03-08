package vidada.client;

import java.util.List;

import archimedesJ.services.IService;

/**
 * Primary Vidada-Client Service which manages all Vidada clients.
 * 
 * @author IsNull
 *
 */
public interface IVidadaClientManager extends IService {

	/**
	 * Gets the active client.
	 * @return
	 */
	IVidadaClientFacade getActive();

	/**
	 * Add the given client.
	 * @param server
	 */
	void addClient(IVidadaClient client);

	/**
	 * Returns all clients currently known to the client manager
	 * @return
	 */
	List<IVidadaClientFacade> getAllClients();

}
