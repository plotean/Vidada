package vidada.dal;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

abstract class JPARepository {
	protected EntityManager getEntityManager(){
		return JPAConfiguration.instance().getDefaultEntityManager();
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
