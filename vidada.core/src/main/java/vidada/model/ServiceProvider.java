package vidada.model;


import vidada.model.security.CredentialManager;
import vidada.model.security.ICredentialManager;
import vidada.streaming.IStreamService;
import vidada.streaming.StreamService;
import archimedesJ.services.ILocator;
import archimedesJ.services.ISelectionService;
import archimedesJ.services.IService;
import archimedesJ.services.SelectionService;
import archimedesJ.services.ServiceLocator;


/**
 * 
 * ServiceProvider is a Singleton backed up with a ServiceLocator, 
 * thus provides access to often used service instances.
 * 
 * @author pascal.buettiker
 *
 */
public class ServiceProvider implements ILocator {

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

		System.out.println("config services...");
		serviceLocator.registerSingleton(ISelectionService.class, SelectionService.class);
		serviceLocator.registerSingleton(ICredentialManager.class, CredentialManager.class);
		serviceLocator.registerSingleton(IStreamService.class, StreamService.class);
		System.out.println("config services done...");
	}




	public void shutdown(){

	}
}