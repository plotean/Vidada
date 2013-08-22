package vidada.model.security;

import java.util.List;

import archimedesJ.io.locations.Credentials;

public interface ICredentialManager {

	public static interface AuthProvider {
		Credentials authenticate(String domain, String description);
	}

	/**
	 * Requests authentication for the given domain
	 * 
	 * @param domain
	 * @param description
	 * @return
	 */
	public Credentials requestAuthentication(String domain, String description, boolean useKeyStore);

	/**
	 * Registers the given auth provider
	 * @param authProvider
	 */
	public void register(AuthProvider authProvider);


	/**
	 * Get the credentials for the given domain
	 * @param domain
	 * @return
	 * @throws AuthenticationRequieredException Thrown when not authenticated
	 */
	public Credentials creditalsFor(String domain) throws AuthenticationRequieredException;

	/**
	 * Store the credentials for the given domain
	 * @param domain
	 * @param credentials
	 * @throws AuthenticationRequieredException
	 */
	public void storeCredentials(String domain, Credentials credentials) throws AuthenticationRequieredException;

	/**
	 * Remove the saved credentials for the given domain
	 * @param domain
	 * @return
	 */
	public boolean removeCredentials(String domain);


	/**
	 * Returns all stored Credentials
	 * @return
	 * @throws AuthenticationRequieredException
	 */
	public List<StoredCredentials>  getAllStoredCredentials() throws AuthenticationRequieredException;

	/**
	 * Remove all stored Credentials
	 */
	public void clearCredentialStore() throws AuthenticationRequieredException;
}
