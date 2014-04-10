package vidada.dal;

import vidada.server.dal.repositories.IRepository;
import archimedes.core.services.ServiceLocator;

class RepositoryManager {

	private final ServiceLocator serviceLocator = new ServiceLocator();

	/**
	 * 
	 * @param iclazz
	 * @return
	 */
	public <T extends IRepository> T resolve(Class<T> iclazz) {
		return serviceLocator.resolve(iclazz);
	}

	/**
	 * Registers the given repository
	 * @param inter
	 * @param implType
	 */
	public <I extends IRepository, T extends I> void register(Class<I> inter, Class<T> implType) {
		serviceLocator.registerSingleton(inter, implType);
	}

	/**
	 * Registers the given repository instance
	 * @param inter
	 * @param instance
	 */
	public <I extends IRepository, T extends I> void register(Class<I> inter, T instance) {
		serviceLocator.registerInstance(inter, instance);
	}

}
