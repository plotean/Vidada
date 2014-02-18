package vidada.model.security;

import vidada.model.settings.DataBaseSettingsManager;
import vidada.model.settings.DatabaseSettings;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.security.Credentials;
import archimedesJ.util.Debug;

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


	private DatabaseSettings getDBSettings(){
		return DataBaseSettingsManager.getSettings();
	}


	@Override
	public boolean isProtected() {
		return getDBSettings().getPasswordHash() != null;
	}

	@Override
	public void protect(Credentials credentials) {

		if(!isProtected())
		{
			DatabaseSettings settings = getDBSettings();
			byte[] hash = KeyPad.hashKey(credentials.getPassword());
			settings.setPasswordHash(hash);

			if(authenticate(credentials)){
				byte[] cryptoBlock = settings.getCryptoBlock();
				settings.setCryptoBlock(keyPadCrypter.enCrypt(cryptoBlock, credentials.getUserSecret()));
				DataBaseSettingsManager.persist(settings);
				ProtectedEvent.fireEvent(this, EventArgsG.Empty);
			}
		}
	}


	@Override
	public void removeProtection(Credentials credentials) throws AuthenticationException {
		if(isProtected())
		{
			if(checkPassword(credentials))
			{
				// password seems to be correct
				if(!isAuthenticated())
					authenticate(credentials);

				try {
					// save the crypto block unencrypted in the db
					getDBSettings().setCryptoBlock(getCryptoPad());
				} catch (AuthenticationRequieredException e) {
					e.printStackTrace();
				}

				// finally remove the password hash to indicate that 
				// this db is not protected
				DatabaseSettings settings = getDBSettings();
				settings.setPasswordHash(null);
				DataBaseSettingsManager.persist(settings);

				ProtectionRemovedEvent.fireEvent(this, EventArgsG.Empty);
			}
		}
	}

	private Credentials authCredentials = null;

	@Override
	public boolean authenticate(Credentials credentials) {

		if(!isAuthenticated())
		{
			if(checkPassword(credentials))
			{
				// password seems to be correct
				// we can now derive the user secret from the password

				authCredentials = credentials;
				System.out.println("PrivacyService: Authentification successful.");
				return true;
			}
		}else {
			return true;
		}
		return false;
	}

	private boolean checkPassword(Credentials credentials){
		byte[] hash = getDBSettings().getPasswordHash();
		byte[] userPassHash = KeyPad.hashKey(credentials.getPassword());

		System.out.println("checkPassword: " + Debug.toString(hash) + " == " + Debug.toString(userPassHash));
		return KeyPad.equals(hash, userPassHash);
	}



	@Override
	public boolean isAuthenticated() {
		return authCredentials != null;
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
				byte[] userKey = getCredentials().getUserSecret(); // throws AuthenticationRequieredException
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
	@Override
	public Credentials getCredentials() throws AuthenticationRequieredException {
		if(!isAuthenticated())
			throw new AuthenticationRequieredException();
		return authCredentials;
	}



}
