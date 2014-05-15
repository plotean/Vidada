package vidada.server.services;

import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.security.AuthenticationRequiredException;
import archimedes.core.security.Credentials;
import archimedes.core.security.StoredCredentials;
import archimedes.core.util.Lists;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.ICredentialRepository;

import java.util.List;

public class CredentialStore extends VidadaServerService implements ICredentialStore {

	protected CredentialStore(VidadaServer server) {
		super(server);
	}

	private final ICredentialRepository repository = getRepository(ICredentialRepository.class);


	@Override
	public Credentials creditalsFor(String domain)
			throws AuthenticationRequiredException {
		// TODO Implement
		/*
		StoredCredentials credentials = findCredentials(domain);
		return credentials != null ? credentials.getCredentials(privacyService.getCryptoPad()) : null;
		 */
		throw new NotImplementedException();
	}

	@Override
	public void storeCredentials(String domain, Credentials credentials)
			throws AuthenticationRequiredException {
		// TODO Implement
		/*
		StoredCredentials existingCredentials = findCredentials(domain);
		if(existingCredentials != null){
			existingCredentials.setCredentials(credentials, privacyService.getCryptoPad());
			repository.update(existingCredentials);
		}else {
			StoredCredentials newCredentials = new StoredCredentials(
					credentials,
					domain,
					privacyService.getCryptoPad());
			repository.store(newCredentials);
		}
		 */
		throw new NotImplementedException();
	}

	@Override
	public boolean removeCredentials(String domain) {
		StoredCredentials credentials = findCredentials(domain);
		if(credentials != null){
			repository.delete(credentials);
			return true;
		}
		return false;
	}


	@SuppressWarnings("serial")
	private StoredCredentials findCredentials(final String domain){
		List<StoredCredentials> credentials = repository.queryByDomain(domain);
		return !credentials.isEmpty() ? credentials.get(0) : null;
	}

	@Override
	public List<StoredCredentials> getAllStoredCredentials()
			throws AuthenticationRequiredException {

		List<StoredCredentials> libs = repository.getAllCredentials();
		return Lists.newList(libs);
	}

	@Override
	public void clearCredentialStore() throws AuthenticationRequiredException {
		for (StoredCredentials credentials : getAllStoredCredentials()) {
			repository.delete(credentials);
		}
	}
}
