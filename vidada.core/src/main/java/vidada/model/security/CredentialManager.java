package vidada.model.security;

import archimedes.core.exceptions.NotImplementedException;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;

public class CredentialManager implements ICredentialManager {


	@Override
	public Credentials requestAuthentication(String domain, String description, CredentialType type, ICredentialsChecker checker, boolean useKeyStore) {

		System.out.println("requestAuthentication for " + domain);

		Credentials credentials = null;
		if(useKeyStore){
			throw new NotImplementedException(); // TODO
			/*
			try {
				credentials = creditalsFor(domain);
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}*/
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
							/*
							try {
								System.out.println("CredentialManager: Storing credentials for " + domain);
								storeCredentials(domain, credentials);
							} catch (AuthenticationRequieredException e) {
								e.printStackTrace();
							}*/
							throw new NotImplementedException(); // TODO
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

	transient private ICredentialsProvider authProvider;
	@Override
	public void register(ICredentialsProvider authProvider) {
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
