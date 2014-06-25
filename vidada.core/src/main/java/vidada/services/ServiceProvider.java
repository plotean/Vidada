package vidada.services;


import archimedes.core.services.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.handlers.MediaPresenterService;
import vidada.model.security.CredentialManager;
import vidada.model.security.ICredentialManager;


/**
 * 
 * ServiceProvider is a Singleton backed up with a ServiceLocator, 
 * thus provides access to often used service instances.
 * 
 * @author pascal.buettiker
 *
 */
public class ServiceProvider implements ILocator {

    private static final Logger logger = LogManager.getLogger(ServiceProvider.class.getName());


    public interface IServiceRegisterer { void registerServices(ServiceLocator locator); }



	// singleton --->
	private static ServiceProvider instance = null;

	public synchronized static ServiceProvider getInstance(){
		if(instance == null)
		{
			instance = new ServiceProvider();
		}
		return instance;
	}

	/**
	 * Resolve the given type to a service instance
	 * @param iclazz
	 * @return
	 */
	public static <T extends IService> T Resolve(Class<T> iclazz) {
		return ServiceProvider.getInstance().resolve(iclazz);
	}

	// <--- singleton


	//
	// ServiceProvider Implementation
	//

	private final ServiceLocator serviceLocator;


	private ServiceProvider(){
		// singleton constructor
		serviceLocator = new ServiceLocator();
	}

	/**
	 * Configures and registers the application services
	 */
	public void startup(IServiceRegisterer registerer) 
	{
		configServices();
		registerer.registerServices(serviceLocator);
	}

	@Override
	public <T extends IService> T resolve(Class<T> iclazz) {
		return serviceLocator.resolve(iclazz);
	}

	/**
	 * Config all services of this application
	 */
	private void configServices(){

        logger.info("Config global services...");

		serviceLocator.registerSingleton(ISelectionService.class, SelectionService.class);
		serviceLocator.registerSingleton(ICredentialManager.class, CredentialManager.class);
		serviceLocator.registerSingleton(IMediaPresenterService.class, MediaPresenterService.class);

        logger.info("Config services done.");
	}




	public void shutdown(){

	}
}