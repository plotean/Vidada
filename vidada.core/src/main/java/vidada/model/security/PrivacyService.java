package vidada.model.security;

import vidada.model.settings.DatabaseSettings;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.events.EventArgs;
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


	private final EventHandlerEx<EventArgs> ProtectedEvent = new EventHandlerEx<EventArgs>();
	private final EventHandlerEx<EventArgs> ProtectionRemovedEvent = new EventHandlerEx<EventArgs>();

	private final IByteBufferEncryption keyPadCrypter = new XORByteCrypter();


	public PrivacyService(){
	}


	@Override
	public IEvent<EventArgs> getProtected() {return ProtectedEvent;}

	@Override
	public IEvent<EventArgs> getProtectionRemoved() {return ProtectionRemovedEvent;}


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


			try {
				authenticate(password);
				byte[] cryptoBlock = getDBSettings().getCryptoBlock();
				getDBSettings().setCryptoBlock(keyPadCrypter.enCrypt(cryptoBlock, getUserKey()));
				getDBSettings().persist();

				ProtectedEvent.fireEvent(this, EventArgsG.Empty);

			} catch (AuthenticationException e) {
				e.printStackTrace();
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}

		}
	}


	@Override
	public void removeProtection(String oldPassword) throws AuthenticationException {
		if(isProtected())
		{
			if(checkPassword(oldPassword))
			{
				// password seems to be correct
				if(!isAuthenticated())
					authenticate(oldPassword);

				try {
					// save the crypto block unencrypted in the db
					getDBSettings().setCryptoBlock(getCryptoPad());
				} catch (AuthenticationRequieredException e) {
					e.printStackTrace();
				}

				byte[] oldUserSecret = KeyPad.calculateSecret(oldPassword.getBytes());

				// finally remove the password hash to indicate that 
				// this db is not protected
				getDBSettings().setPasswordHash(null);
				getDBSettings().persist();

				ProtectionRemovedEvent.fireEvent(this, EventArgsG.build(oldUserSecret));
			}
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

	private byte[] plainCryptoPad;

	/**
	 * Gets the un encrypted crypto pad
	 */
	@Override
	public byte[] getCryptoPad() throws AuthenticationRequieredException{

		if(plainCryptoPad == null){
			if(isProtected())
			{
				byte[] userKey = getUserKey(); // throws AuthenticationRequieredException
				byte[] cryptoPadEncrypted = getDBSettings().getCryptoBlock();

				// we have to decrypt the cryptoPad
				plainCryptoPad = keyPadCrypter.deCrypt(cryptoPadEncrypted, userKey);
			}else{
				// on unprotected systems, the crypto block stored in the DB is not encrypted
				plainCryptoPad = getDBSettings().getCryptoBlock();
			}
		}
		return plainCryptoPad;
	}


	/**
	 * Gets the user secret which is a calculated value derived from the 
	 * users original password. Therefore, the user must be authenticated
	 * in order to calculate the secret.
	 * 
	 */
	private byte[] getUserKey() throws AuthenticationRequieredException {
		if(!isAuthenticated())
			throw new AuthenticationRequieredException();
		return userSecret;
	}



}
