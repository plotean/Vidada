package vidada.server.repositories;

import java.util.List;

import vidada.model.security.StoredCredentials;

public interface ICredentialRepository extends IRepository<StoredCredentials> {

	void update(StoredCredentials existingCredentials);

	void store(StoredCredentials newCredentials);

	void delete(StoredCredentials credentials);

	List<StoredCredentials> queryByDomain(String domain);

	List<StoredCredentials> getAllCredentials();
}
