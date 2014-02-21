package vidada.dal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import vidada.aop.IUnitOfWorkService;

public abstract class JPARepository {

	private final IUnitOfWorkService<EntityManager> unitOfWorkService;

	protected JPARepository(IUnitOfWorkService<EntityManager> unitOfWorkService){
		this.unitOfWorkService = unitOfWorkService;
	}

	protected EntityManager getEntityManager(){
		return unitOfWorkService.getCurrentContext();
	}


	protected <T> T firstOrDefault(TypedQuery<T> query){
		List<T> results = query.getResultList();
		if(!results.isEmpty())
			return results.get(0);
		else {
			return null;
		}
	}
}
