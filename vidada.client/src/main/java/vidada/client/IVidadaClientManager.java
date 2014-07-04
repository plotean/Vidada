package vidada.client;

import archimedes.core.events.EventArgs;
import archimedes.core.events.IEvent;
import archimedes.core.services.IService;

import java.util.List;

/**
 * Primary Vidada-Client Service which manages all Vidada clients.
 * 
 * @author IsNull
 *
 */
public interface IVidadaClientManager extends IService {

    /**
     * Raised when the active client has been changed.
     * @return
     */
    IEvent<EventArgs> getActiveClientChanged();


	/**
	 * Gets the active client.
	 * @return
	 */
	IVidadaClientFacade getActive();

	/**
	 * Add the given client.
	 * @param client
	 */
	void addClient(IVidadaClient client);

	/**
	 * Returns all clients currently known to the client manager
	 * @return
	 */
	List<IVidadaClientFacade> getAllClients();

}
