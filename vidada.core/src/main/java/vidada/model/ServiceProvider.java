package vidada.model;


import vidada.model.images.IThumbnailService;
import vidada.model.images.ThumbnailService;
import vidada.model.images.cache.crypto.CryptedCacheUtil;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaService;
import vidada.model.media.store.IMediaStoreService;
import vidada.model.media.store.MediaStoreService;
import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.CredentialManager;
import vidada.model.security.ICredentialManager;
import vidada.model.security.IPrivacyService;
import vidada.model.security.PrivacyService;
import vidada.model.settings.GlobalSettings;
import vidada.model.tags.ITagService;
import vidada.model.tags.TagService;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoryLocation;
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

		//serviceLocator.registerSingleton(IConnectivityService.class, ConnectivityService.class);
		serviceLocator.registerSingleton(IPrivacyService.class, PrivacyService.class);
		serviceLocator.registerSingleton(ISelectionService.class, SelectionService.class);
		serviceLocator.registerSingleton(IMediaService.class, MediaService.class);
		serviceLocator.registerSingleton(ITagService.class, TagService.class);
		serviceLocator.registerSingleton(IThumbnailService.class, ThumbnailService.class);
		serviceLocator.registerSingleton(ICredentialManager.class, CredentialManager.class);
		serviceLocator.registerSingleton(IMediaStoreService.class, MediaStoreService.class);


		//serviceLocator.registerSingleton(IImageCacheService.class, VidadaImageCache.class);

		registerProtectionHandler();

		System.out.println("config services done...");
	}

	private void registerProtectionHandler(){

		final IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);

		if(privacyService != null)
		{
			privacyService.getProtected().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						final DirectoryLocation localCache = DirectoryLocation.Factory
								.create(GlobalSettings.getInstance().getAbsoluteCachePath());

						System.out.println("ServiceProvider: " + privacyService.getCredentials().toString());
						CryptedCacheUtil.encryptWithPassword(localCache, privacyService.getCredentials());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});


			privacyService.getProtectionRemoved().add(new EventListenerEx<EventArgs>() {

				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						final DirectoryLocation localCache = DirectoryLocation.Factory
								.create(GlobalSettings.getInstance().getAbsoluteCachePath());

						CryptedCacheUtil.removeEncryption( localCache, privacyService.getCredentials());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});
		}else
			System.err.println("CacheKeyProvider: IPrivacyService is not avaiable!");
	}



	public void shutdown(){

	}
}