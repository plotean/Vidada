package vidada.server.dal;

import vidada.aop.IUnitOfWorkRunner;
import vidada.server.dal.repositories.IRepository;

/**
 * Manages the data access layer of Vidada, such as Repositories and Unit-of-work management.
 * 
 * @author IsNull
 *
 */
public interface IVidadaDALService {

	/**
	 * Resolves the Repository implementation.
	 * A repository always implements the {@link IRepository} interface.
	 * 
	 * 
	 * @param iclazz
	 * @return
	 */
	public <T extends IRepository> T getRepository(Class<T> iclazz);

	/**
	 * Get the Unit of Work manager
	 * @return
	 */
	public IUnitOfWorkRunner getUnitOfWorkRunner();
}
