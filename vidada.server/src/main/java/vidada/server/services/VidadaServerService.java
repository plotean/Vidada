package vidada.server.services;

import vidada.server.VidadaServer;
import vidada.server.dal.IVidadaDALService;
import vidada.server.dal.repositories.IRepository;

import java.util.concurrent.Callable;

/**
 * Base-class for all Vidada server services
 * @author IsNull
 *
 */
public abstract class VidadaServerService {

	private final VidadaServer server;
	private final IVidadaDALService dalService;


	/**
	 * Creates a new server service for the given server
	 * @param server
	 */
	protected VidadaServerService(VidadaServer server){
		this.server = server;
		dalService = server.getDalService();

		if(dalService == null) throw new IllegalArgumentException("The provided server has NULL DAL Service.");
	}

	/**
	 * Get the parent server of this service
	 * @return
	 */
	protected VidadaServer getServer(){
		return server;
	}

	/**
	 * Run the given code in a Unit of work.
	 * 
	 * Nested calls are supported as long as the same Unit-Of-Work service is used.
	 * (Which should always be the case.)
	 * @param code
	 */
	protected void runUnitOfWork(Runnable code){
		dalService.getUnitOfWorkRunner().runUnit(code);
	}

	/**
	 * Run the given code in a Unit of work and returns the calculated result.
	 * @param code
	 * @return
	 */
	protected <V> V runUnitOfWork(Callable<V> code){
		return dalService.getUnitOfWorkRunner().runUnit(code);
	}

    /**
     * Resolves the given Repository
     * @param iclazz
     * @param <T>
     * @return
     */
	protected <T extends IRepository> T getRepository(Class<T> iclazz){
		return dalService.getRepository(iclazz);
	}

}
