package vidada.repositories;

import archimedesJ.services.ILocator;
import archimedesJ.services.IService;
import archimedesJ.services.ServiceLocator;

public class RepositoryProvider implements ILocator {

	// singleton --->
	private static RepositoryProvider instance = null;

	public synchronized static RepositoryProvider getInstance(){
		if(instance == null)
		{
			instance = new RepositoryProvider();
		}
		return instance;
	}

	private RepositoryProvider() { }

	/**
	 * Resolve the given type to a service instance
	 * @param iclazz
	 * @return
	 */
	public static <T extends IService> T Resolve(Class<T> iclazz) {
		return RepositoryProvider.getInstance().resolve(iclazz);
	}


	private final ServiceLocator serviceLocator = new ServiceLocator();


	@Override
	public <T extends IService> T resolve(Class<T> iclazz) {
		return serviceLocator.resolve(iclazz);
	}

	/**
	 * Registers the given repository
	 * @param inter
	 * @param implType
	 */
	public <I extends IRepository<?>, T extends I> void register(Class<I> inter, Class<T> implType) {
		serviceLocator.registerSingleton(inter, implType);
	}

}
