package vidada.model.security;

import vidada.model.settings.DatabaseSettings;
import archimedesJ.crypto.KeyPad;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

/**
 * Implementation of IPrivacyService
 * 
 * @author IsNull
 *
 */
public class PrivacyService implements IPrivacyService{


	private final EventHandlerEx<EventArgsG<byte[]>> ProtectedEvent = new EventHandlerEx<EventArgsG<byte[]>>();
	private final EventHandlerEx<EventArgsG<byte[]>> ProtectionRemovedEvent = new EventHandlerEx<EventArgsG<byte[]>>();


	public PrivacyService(){
		System.out.println("starting PrivacyService...");
	}



	@Override
	public IEvent<EventArgsG<byte[]>> getProtected() {return ProtectedEvent;}

	@Override
	public IEvent<EventArgsG<byte[]>> getProtectionRemoved() {return ProtectionRemovedEvent;}


	private DatabaseSettings dbSettings;

	private DatabaseSettings getDBSettings(){
		if(dbSettings == null)
			dbSettings = DatabaseSettings.getSettings();
		return dbSettings;
	}


	@Override
	public boolean isProtected() {
		return getDBSettings().getPasswordHash() != null;
	}

	@Override
	public void protect(String password) {

		if(!isProtected())
		{
			byte[] hash = KeyPad.hashKey(password);
			getDBSettings().setPasswordHash(hash);
			getDBSettings().persist();


			try {
				authenticate(password);
				ProtectedEvent.fireEvent(this, EventArgsG.build(getUserSecret()));

			} catch (AuthenticationException e) {
				e.printStackTrace();
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}

		}else {
			//throw new NotSupportedException("You can not protect a already protected library.");

		}
	}


	@Override
	public void removeProtection(String oldPassword) throws AuthenticationException {
		if(isProtected())
		{
			if(checkPassword(oldPassword))
			{
				// password seems to be correct

				byte[] oldUserSecret = KeyPad.calculateSecret(oldPassword.getBytes());

				// finally remove the password hash to indicate that 
				// this db is not protected
				getDBSettings().setPasswordHash(null);

				ProtectionRemovedEvent.fireEvent(this, EventArgsG.build(oldUserSecret));
			}else
				throw new AuthenticationException();
		}
	}

	private byte[] userSecret = null;

	@Override
	public boolean authenticate(String password) throws AuthenticationException {

		if(!isAuthenticated())
		{
			if(checkPassword(password))
			{
				// password seems to be correct
				// we can now derive the user secret from the password

				userSecret = KeyPad.calculateSecret(password.getBytes());
				System.out.println("usersecret set to: " + userSecret);
				return true;
			}else
				throw new AuthenticationException();
		}
		return false;
	}

	private boolean checkPassword(String password){
		byte[] hash = getDBSettings().getPasswordHash();
		byte[] userPassHash = KeyPad.hashKey(password);

		System.out.println("checkPassword: " + KeyPad.toString(hash) + " == " + KeyPad.toString(userPassHash));
		return KeyPad.equals(hash, userPassHash);
	}



	@Override
	public boolean isAuthenticated() {
		return userSecret != null;
	}

	/**
	 * Gets the user secret which is a calculated value derived from the 
	 * users original password. Therefore, the user must be authenticated
	 * in order to calculate the secret.
	 * 
	 */
	@Override
	public byte[] getUserSecret() throws AuthenticationRequieredException {

		if(!isAuthenticated())
			throw new AuthenticationRequieredException();

		return userSecret;
	}

}
