package vidada.model.security;

import archimedesJ.events.EventArgs;
import archimedesJ.events.IEvent;
import archimedesJ.services.IService;

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
	public abstract void protect(String password);


	/**
	 * Removes the protection from this media database
	 * 
	 * @param oldPassword The old password
	 */
	public abstract void removeProtection(String oldPassword) throws AuthenticationException;


	/**
	 * Authenticate with the given password
	 * @param password
	 * @return Returns true if successfully authenticated
	 * @throws AuthenticationException Thrown, if the password is wrong
	 */
	public abstract boolean authenticate(String password);

	/**
	 * Is the current system user authenticated?
	 * @return
	 */
	public boolean isAuthenticated();

	/**
	 * Gets the crypto-pad used to encrypt/decrypt all data
	 * 
	 * If this database is protected, previous authentication is required
	 * in order to get this crypto pad.
	 * 
	 * @return
	 * @throws AuthenticationRequieredException Thrown if the current user is not authenticated. 
	 * @see <code>authenticate(String password)</code>
	 */
	public abstract byte[] getCryptoPad() throws AuthenticationRequieredException;

	// Credential management

}
