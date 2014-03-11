package vidada.server.services;

import java.util.List;

import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.StoredCredentials;
import archimedesJ.security.Credentials;

public interface ICredentialStore {

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
