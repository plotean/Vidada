package vidada.client.composite;

import vidada.client.VidadaClientManager;

/**
 * Abstract base class for all client services
 * @author IsNull
 *
 */
public abstract class ClientService {

	private final VidadaClientManager manager;

	protected ClientService(VidadaClientManager manager){
		this.manager = manager;
	}

	protected VidadaClientManager getClientManager(){
		return manager;
	}
}
