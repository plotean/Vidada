package vidada.model.security;

import java.util.List;

import vidada.model.ServiceProvider;
import vidada.server.repositories.ICredentialRepository;
import vidada.server.repositories.RepositoryProvider;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;
import archimedesJ.util.Lists;

public class CredentialManager implements ICredentialManager {

	private final IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);
	private final ICredentialRepository repository = RepositoryProvider.Resolve(ICredentialRepository.class);

	@Override
	public Credentials creditalsFor(String domain)
			throws AuthenticationRequieredException {
		StoredCredentials credentials = findCredentials(domain);
		return credentials != null ? credentials.getCredentials(privacyService.getCryptoPad()) : null;
	}

	@Override
	public void storeCredentials(String domain, Credentials credentials)
			throws AuthenticationRequieredException {

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


	@Override
	public Credentials requestAuthentication(String domain, String description, CredentialType type, CredentialsChecker checker, boolean useKeyStore) {

		System.out.println("requestAuthentication for " + domain);

		Credentials credentials = null;
		if(useKeyStore){
			try {
				credentials = creditalsFor(domain);
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}
		}

		if(credentials == null){
			// we have no saved credentials, we need to ask the user
			if(authProvider == null)
				throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

			// as for correct authentication until success or abort
			while (true) {

				System.out.println("CredentialManager: Asking user for credentials...");
				credentials = authProvider.authenticate(domain, description, type);

				if(credentials != null){
					if(checker.check(credentials))
					{
						System.out.println("CredentialManager: User entered correct credentials.");

						if(credentials.isRemember()){
							try {
								System.out.println("CredentialManager: Storing credentials for " + domain);

								storeCredentials(domain, credentials);
							} catch (AuthenticationRequieredException e) {
								e.printStackTrace();
							}
						}

						// Credentials were correct
						break;
					}else{
						System.err.println("CredentialManager: User entered wrong credentials.");
					}
				}else {
					System.out.println("CredentialManager: User Canceled entering credentials.");
					// User canceled
					break;
				}

			}
		}

		System.out.println("CredentialManager: returning credentials -> " + credentials);
		return credentials;
	}

	transient private CredentialsProvider authProvider;
	@Override
	public void register(CredentialsProvider authProvider) {
		this.authProvider = authProvider;
	}


	@Override
	public Credentials requestCredentials(String description,
			CredentialType type) {
		if(authProvider == null)
			throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

		return authProvider.authenticate(null, description, type);
	}

	@Override
	public Credentials requestNewCredentials(String description,
			CredentialType type) {
		if(authProvider == null)
			throw new NotSupportedException("No authProvider was registered. -> requestAuthentication");

		Credentials credentials = requestCredentials(description, type);

		if(credentials != null){
			Credentials credentialsConf = requestCredentials("Please confirm your credentials.", type);
			if(!credentials.equals(credentialsConf)){
				System.err.println("Credential confirmation failed.");
				credentials = null;
			}
		}
		return credentials;
	}


}
