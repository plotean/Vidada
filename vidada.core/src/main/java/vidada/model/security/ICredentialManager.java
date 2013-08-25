package vidada.model.security;

import java.util.List;

import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;
import archimedesJ.services.IService;

public interface ICredentialManager extends IService{

	/**
	 * Delegate to check credentials
	 * @author IsNull
	 *
	 */
	public static interface CredentialsChecker {
		/**
		 * Check the given credentials
		 * @param credentials
		 * @return
		 */
		boolean check(Credentials credentials);
	}

	/**
	 * Callback delegate for authentication details
	 * @author IsNull
	 *
	 */
	public static interface CredentialsProvider {
		/**
		 * Requests user authentication which usually results in prompting the user for credentials
		 * @param domain
		 * @param description
		 * @return
		 */
		Credentials authenticate(String domain, String description, CredentialType type);
	}

	/**
	 * Requests authentication for the given domain. 
	 * If the user enters wrong information, he will be prompted again
	 * a) either until the Credentials are correct
	 * b) or the user cancels the dialog.
	 * This may cause blocking of this thread  since a auth dialog may be shown to the user.
	 *
	 * 
	 * @param domain The domain id
	 * @param description A request description for the user
	 * @param type The type
	 * @param useKeyStore Shall remembered keys being used to authenticate?
	 * @return On success, returns the valid Credentials.
	 */
	public Credentials requestAuthentication(String domain, String description, CredentialType type, CredentialsChecker checker, boolean useKeyStore);

	/**
	 * Requests new credentials, the user has to confirm the entered credentials
	 * @param description
	 * @param type
	 * @return
	 */
	public Credentials requestNewCredentials(String description, CredentialType type);

	/**
	 * Requests credentials without checking them and without using a key-store
	 * @param description
	 * @param type
	 * @return
	 */
	public Credentials requestCredentials(String description, CredentialType type);



	/**
	 * Registers the given Credentials provider
	 * @param authProvider
	 */
	public void register(CredentialsProvider authProvider);


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
