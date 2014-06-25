package vidada.server.impl;

import archimedes.core.crypto.IByteBufferEncryption;
import archimedes.core.crypto.KeyPad;
import archimedes.core.crypto.XORByteCrypter;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.security.AuthenticationException;
import archimedes.core.security.AuthenticationRequiredException;
import archimedes.core.security.Credentials;
import archimedes.core.util.Debug;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.server.VidadaServer;
import vidada.server.services.VidadaServerService;
import vidada.server.settings.DatabaseSettings;
import vidada.server.settings.IDatabaseSettingsService;

import java.util.concurrent.Callable;

/**
 * Implementation of IPrivacyService
 * 
 * @author IsNull
 *
 */
public class PrivacyService extends VidadaServerService implements IPrivacyService{

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(PrivacyService.class.getName());

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

						} catch (AuthenticationRequiredException e) {
                            logger.error(e);
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
                logger.info("PrivacyService: Authentication successful.");
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
        logger.debug("checkPassword: " + Debug.toString(hash) + " == " + Debug.toString(userPassHash));
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
	public byte[] getCryptoPad() throws AuthenticationRequiredException {

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
						} catch (AuthenticationRequiredException e) {
                            logger.debug(e);
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
	public Credentials getCredentials() throws AuthenticationRequiredException {
		if(!isAuthenticated())
			throw new AuthenticationRequiredException();
		return authCredentials;
	}



}
