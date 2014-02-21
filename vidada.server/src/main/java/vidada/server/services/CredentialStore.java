package vidada.server.services;

import java.util.List;

import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.StoredCredentials;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.ICredentialRepository;
import vidada.services.ICredentialStore;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.security.Credentials;
import archimedesJ.util.Lists;

public class CredentialStore extends VidadaServerService implements ICredentialStore {

	protected CredentialStore(VidadaServer server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	private final ICredentialRepository repository = getRepository(ICredentialRepository.class);


	@Override
	public Credentials creditalsFor(String domain)
			throws AuthenticationRequieredException {
		// TODO Implement
		/*
		StoredCredentials credentials = findCredentials(domain);
		return credentials != null ? credentials.getCredentials(privacyService.getCryptoPad()) : null;
		 */
		throw new NotImplementedException();
	}

	@Override
	public void storeCredentials(String domain, Credentials credentials)
			throws AuthenticationRequieredException {
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
			throws AuthenticationRequieredException {

		List<StoredCredentials> libs = repository.getAllCredentials();
		return Lists.newList(libs);
	}

	@Override
	public void clearCredentialStore() throws AuthenticationRequieredException {
		for (StoredCredentials credentials : getAllStoredCredentials()) {
			repository.delete(credentials);
		}
	}
}
