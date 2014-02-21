package vidada.server.dal.repositories;

import java.util.List;

import vidada.model.security.StoredCredentials;

public interface ICredentialRepository extends IRepository {

	void update(StoredCredentials existingCredentials);

	void store(StoredCredentials newCredentials);

	void delete(StoredCredentials credentials);

	List<StoredCredentials> queryByDomain(String domain);

	List<StoredCredentials> getAllCredentials();
}
