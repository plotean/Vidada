package vidada.dal;

import archimedesJ.aop.IUnitOfWorkService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


public abstract class JPARepository {

	private final IUnitOfWorkService<EntityManager> unitOfWorkService;

	protected JPARepository(IUnitOfWorkService<EntityManager> unitOfWorkService){
		this.unitOfWorkService = unitOfWorkService;
	}

	protected  IUnitOfWorkService<EntityManager> getUnitOfWorkService(){
		return unitOfWorkService;
	}

	/**
	 * Returns the current Entity-Manager
	 * @return
	 */
	protected EntityManager getEntityManager(){
		return unitOfWorkService.getCurrentUnitContext();
	}


	/**
	 * Returns the first row from the given result set or NULL if not available.
	 * @param query
	 * @return
	 */
	protected <T> T firstOrDefault(TypedQuery<T> query){
		List<T> results = query.getResultList();
		if(!results.isEmpty())
			return results.get(0);
		else {
			return null;
		}
	}
}
