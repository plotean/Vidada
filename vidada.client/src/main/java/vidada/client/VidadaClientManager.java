package vidada.client;

import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import vidada.client.facade.VidadaClientFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

    private final EventHandlerEx<EventArgs> activeClientChanged = new EventHandlerEx<EventArgs>();

    /**
     * Raised when the Property Active (Client) has changed
     * @return
     */
    @Override
    public IEvent<EventArgs> getActiveClientChanged() { return activeClientChanged;}

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
        activeClientChanged.fireEvent(this, EventArgs.Empty);
	}

    /**{@inheritDoc}*/
	@Override
	public IVidadaClientFacade getActive() {
		return active;
	}

}

