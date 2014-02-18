package vidada.client.services;

import vidada.client.VidadaClientManager;

public abstract class ClientService {

	private final VidadaClientManager manager;

	protected ClientService(VidadaClientManager manager){
		this.manager = manager;
	}

	protected VidadaClientManager getClientManager(){
		return manager;
	}
}
