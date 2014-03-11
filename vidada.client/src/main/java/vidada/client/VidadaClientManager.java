package vidada.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vidada.client.facade.VidadaClientFacade;

public class VidadaClientManager implements IVidadaClientManager {


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

}

