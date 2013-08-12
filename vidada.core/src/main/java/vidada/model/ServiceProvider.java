package vidada.model;


import vidada.model.cache.IImageCacheService;
import vidada.model.cache.VidadaImageCache;
import vidada.model.connectivity.ConnectivityService;
import vidada.model.connectivity.IConnectivityService;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibraryService;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaService;
import vidada.model.security.IPrivacyService;
import vidada.model.security.PrivacyService;
import vidada.model.tags.ITagService;
import vidada.model.tags.TagService;
import archimedesJ.services.ILocator;
import archimedesJ.services.ISelectionService;
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

	public static ServiceProvider getInstance(){
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
	public static <T> T Resolve(Class<T> iclazz) {
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
	public <T> T resolve(Class<T> iclazz) {
		return serviceLocator.resolve(iclazz);
	}

	/**
	 * Config all services of this application
	 */
	private void configServices(){

		System.out.println("config services...");

		serviceLocator.registerSingleton(IConnectivityService.class, ConnectivityService.class);
		serviceLocator.registerSingleton(IPrivacyService.class, PrivacyService.class);
		serviceLocator.registerSingleton(ISelectionService.class, SelectionService.class);
		serviceLocator.registerSingleton(IMediaLibraryService.class, MediaLibraryService.class);
		serviceLocator.registerSingleton(IMediaService.class, MediaService.class);
		serviceLocator.registerSingleton(ITagService.class, TagService.class);
		serviceLocator.registerSingleton(IImageCacheService.class, VidadaImageCache.class);

		System.out.println("config services done...");
	}





	public void shutdown(){

	}
}