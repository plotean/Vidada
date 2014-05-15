package vidada.server.dal.repositories;

import archimedes.core.security.StoredCredentials;

import java.util.List;


public interface ICredentialRepository extends IRepository {

	void update(StoredCredentials existingCredentials);

	void store(StoredCredentials newCredentials);

	void delete(StoredCredentials credentials);

	List<StoredCredentials> queryByDomain(String domain);

	List<StoredCredentials> getAllCredentials();
}
