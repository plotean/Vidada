package vidada.dal.repositorys;

import java.util.List;

import javax.persistence.TypedQuery;

import vidada.dal.JPARepository;
import vidada.model.security.StoredCredentials;
import vidada.repositories.ICredentialRepository;

public class CredentialRepository extends JPARepository implements ICredentialRepository {

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
