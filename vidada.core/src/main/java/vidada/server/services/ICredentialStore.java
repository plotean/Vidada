package vidada.server.services;

import archimedes.core.security.AuthenticationRequiredException;
import archimedes.core.security.Credentials;
import archimedes.core.security.StoredCredentials;

import java.util.List;

public interface ICredentialStore {

	/**
	 * Get the credentials for the given domain
	 * @param domain
	 * @return
	 * @throws AuthenticationRequiredException Thrown when not authenticated
	 */
	public Credentials creditalsFor(String domain) throws AuthenticationRequiredException;

	/**
	 * Store the credentials for the given domain
	 * @param domain
	 * @param credentials
	 * @throws AuthenticationRequiredException
	 */
	public void storeCredentials(String domain, Credentials credentials) throws AuthenticationRequiredException;

	/**
	 * Remove the saved credentials for the given domain
	 * @param domain
	 * @return
	 */
	public boolean removeCredentials(String domain);


	/**
	 * Returns all stored Credentials
	 * @return
	 * @throws AuthenticationRequiredException
	 */
	public List<StoredCredentials>  getAllStoredCredentials() throws AuthenticationRequiredException;

	/**
	 * Remove all stored Credentials
	 */
	public void clearCredentialStore() throws AuthenticationRequiredException;
}
