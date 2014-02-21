package vidada.server.impl;

import java.util.concurrent.Callable;

import vidada.model.security.AuthenticationException;
import vidada.model.security.AuthenticationRequieredException;
import vidada.server.VidadaServer;
import vidada.server.services.VidadaServerService;
import vidada.server.settings.DatabaseSettings;
import vidada.server.settings.IDatabaseSettingsService;
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
public class PrivacyService extends VidadaServerService implements IPrivacyService{

	private final EventHandlerEx<EventArgs> ProtectedEvent = new EventHandlerEx<EventArgs>();
	private final EventHandlerEx<EventArgs> ProtectionRemovedEvent = new EventHandlerEx<EventArgs>();

	private final IByteBufferEncryption keyPadCrypter = new XORByteCrypter();

	public PrivacyService(VidadaServer server) {
		super(server);
	}

	@Override
	public IEvent<EventArgs> getProtected() {return ProtectedEvent;}

	@Override
	public IEvent<EventArgs> getProtectionRemoved() {return ProtectionRemovedEvent;}



	@Override
	public boolean isProtected() {
		return runUnitOfWork(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				DatabaseSettings settings = getServer().getDatabaseSettingsService().getSettings();
				return settings.getPasswordHash() != null;
			}
		});
	}

	@Override
	public void protect(final Credentials credentials) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				if(!isProtected())
				{
					IDatabaseSettingsService databaseSettingsService = getServer().getDatabaseSettingsService();

					DatabaseSettings settings = databaseSettingsService.getSettings();

					byte[] hash = KeyPad.hashKey(credentials.getPassword());
					settings.setPasswordHash(hash);

					if(authenticate(credentials)){
						byte[] cryptoBlock = settings.getCryptoBlock();
						settings.setCryptoBlock(keyPadCrypter.enCrypt(cryptoBlock, credentials.getUserSecret()));
						databaseSettingsService.update(settings);
						ProtectedEvent.fireEvent(this, EventArgsG.Empty);
					}
				}
			}
		});

	}


	@Override
	public void removeProtection(final Credentials credentials) throws AuthenticationException {

		runUnitOfWork(new Runnable() {

			@Override
			public void run() {

				if(isProtected())
				{
					if(checkPassword(credentials))
					{
						// Password seems to be correct
						if(!isAuthenticated())
							authenticate(credentials);

						DatabaseSettings settings = getServer().getDatabaseSettingsService().getSettings();

						try {
							// Save the crypto block unencrypted in the db
							settings.setCryptoBlock(getCryptoPad());

							// Finally remove the password hash to indicate that 
							// this DB is not protected
							settings.setPasswordHash(null);

							getServer().getDatabaseSettingsService().update(settings);

							ProtectionRemovedEvent.fireEvent(this, EventArgsG.Empty);

						} catch (AuthenticationRequieredException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});

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
		DatabaseSettings settings = getServer().getDatabaseSettingsService().getSettings();
		byte[] hash = settings.getPasswordHash();
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

			plainCryptoPad = runUnitOfWork(new Callable<byte[]>() {
				@Override
				public byte[] call() throws Exception {
					byte[] plainCryptoPad;
					DatabaseSettings settings = getServer().getDatabaseSettingsService().getSettings();
					if(isProtected())
					{
						try {
							byte[] userKey = getCredentials().getUserSecret();
							byte[] cryptoPadEncrypted = settings.getCryptoBlock();
							// we have to decrypt the cryptoPad
							plainCryptoPad = keyPadCrypter.deCrypt(cryptoPadEncrypted, userKey);
						} catch (AuthenticationRequieredException e) {
							e.printStackTrace();
							throw e;
						} 
					}else{
						// on unprotected systems, the crypto block stored in the DB is not encrypted
						plainCryptoPad = settings.getCryptoBlock();
					}
					return plainCryptoPad;
				}
			});

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
