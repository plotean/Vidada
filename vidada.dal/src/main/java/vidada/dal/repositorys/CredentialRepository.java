package vidada.dal.repositorys;

import archimedes.core.aop.IUnitOfWorkService;
import archimedes.core.security.StoredCredentials;
import vidada.dal.JPARepository;
import vidada.server.dal.repositories.ICredentialRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class CredentialRepository extends JPARepository implements ICredentialRepository {

	public CredentialRepository(
			IUnitOfWorkService<EntityManager> unitOfWorkService) {
		super(unitOfWorkService);
	}

	@Override
	public void update(StoredCredentials existingCredentials) {
		getEntityManager().merge(existingCredentials);
	}

	@Override
	public void store(StoredCredentials newCredentials) {
		getEntityManager().persist(newCredentials);
	}

	@Override
	public void delete(StoredCredentials credentials) {
		getEntityManager().remove(credentials);
	}

	@Override
	public List<StoredCredentials> queryByDomain(String domain) {
		TypedQuery<StoredCredentials> query = getEntityManager().createQuery("SELECT l from StoredCredentials l WHERE l.domain ='"+domain+"'", StoredCredentials.class);
		return query.getResultList();
	}

	@Override
	public List<StoredCredentials> getAllCredentials() {
		TypedQuery<StoredCredentials> query = getEntityManager().createQuery("SELECT l from StoredCredentials l", StoredCredentials.class);
		return query.getResultList();
	}

}
