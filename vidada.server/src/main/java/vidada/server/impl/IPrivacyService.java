package vidada.server.impl;

import vidada.model.security.AuthenticationException;
import vidada.model.security.AuthenticationRequieredException;
import archimedes.core.events.EventArgs;
import archimedes.core.events.IEvent;
import archimedes.core.security.Credentials;
import archimedes.core.services.IService;

/**
 * Manages privacy settings
 * @author IsNull
 *
 */
public interface IPrivacyService extends IService{



	/**
	 * Occurs when the protection has been removed. THe EventsArgs hold the old user secret
	 * 
	 * @return
	 */
	public abstract IEvent<EventArgs> getProtectionRemoved();

	/**
	 * Occurs when the protection has been applied. The EventsArgs hold the new user secret
	 * @return
	 */
	public abstract IEvent<EventArgs> getProtected();


	/**
	 * Is this media database protected by a password
	 * @return
	 */
	public abstract boolean isProtected();


	/**
	 * Protect this media database with the given password
	 * 
	 * @param password The new password
	 */
	public abstract void protect(Credentials credentials);


	/**
	 * Removes the protection from this media database
	 * 
	 * @param oldPassword The old password
	 */
	public abstract void removeProtection(Credentials credentials) throws AuthenticationException;


	/**
	 * Authenticate with the given password
	 * @param password
	 * @return Returns true if successfully authenticated
	 * @throws AuthenticationException Thrown, if the password is wrong
	 */
	public abstract boolean authenticate(Credentials credentials);

	/**
	 * Is the current system user authenticated?
	 * @return
	 */
	public boolean isAuthenticated();

	/**
	 * Gets the crypto-pad used to encrypt/decrypt all data.
	 * The Crypto-Pad is bound to this Database
	 * 
	 * If this database is protected, previous authentication is required
	 * in order to get this crypto pad.
	 * 
	 * @return
	 * @throws AuthenticationRequieredException Thrown if the current user is not authenticated. 
	 * @see <code>authenticate(String password)</code>
	 */
	public abstract byte[] getCryptoPad() throws AuthenticationRequieredException;

	/**
	 * Gets the currently authenticated user credentials
	 * 
	 * @return
	 * @throws AuthenticationRequieredException
	 */
	Credentials getCredentials() throws AuthenticationRequieredException;

}
