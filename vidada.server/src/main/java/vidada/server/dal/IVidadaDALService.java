package vidada.server.dal;

import vidada.aop.IUnitOfWorkRunner;
import vidada.server.dal.repositories.IRepository;

public interface IVidadaDALService {

	/**
	 * Resolves the Repository implementation.
	 * @param iclazz
	 * @return
	 */
	public <T extends IRepository> T getRepository(Class<T> iclazz);

	/**
	 * 
	 * @return
	 */
	public IUnitOfWorkRunner getUnitOfWorkRunner();
}
