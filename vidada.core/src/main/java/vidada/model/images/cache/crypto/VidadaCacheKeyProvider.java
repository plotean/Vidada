package vidada.model.images.cache.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import vidada.model.ServiceProvider;
import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.IPrivacyService;
import vidada.model.settings.GlobalSettings;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.KeyCurruptedException;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.FileSupport;


/**
 * The cache encryption
 * --------------------
 * As a general remark, the cached images are always encrypted.
 * The difference between the two states is, that in DECRYPTED
 * state, the encryption key-pad is freely available in the root folder
 * while in ENCRYPTED state, the key-pad is itself encrypted with the users
 * password. The key-pad has a role similar to a master password.
 * 
 * 
 * DECRYPTED STATE
 * 
 * In the DECRYPTED state, the encryption keypad is saved
 * as "cache.key" in the root folder of the cache. The 
 * keypad is generated randomly the first time a cache is
 * used.
 * 
 * This basically means that the contents are always
 * encrypted, but the key is freely available in the root.
 * 
 * 
 * 
 * ENCRYPTED STATE
 * 
 * When the cache contents are protected, the "cache.key"
 * gets AES encrypted with the users password and saved as
 * "cache.encrypted". The not encrypted "cache.key" is
 * off course removed.
 * 
 * @author IsNull
 *
 */
public class VidadaCacheKeyProvider implements ICacheKeyProvider{

	private byte[] defaultKeyPad = new byte[]{12,34,65,23,45,23};

	private final static int KEYPAD_SIZE = 20;

	private final static String KeyFileName = "cache.keypad";
	private final static String EncryptedKeyFileName = "cache.encrypted";

	private final IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);
	private final IByteBufferEncryption keyCrypter = new XORByteCrypter();


	public VidadaCacheKeyProvider(){

		final DirectoiryLocation localCache = DirectoiryLocation.Factory.create(GlobalSettings.getInstance().getAbsoluteCachePath());

		if(privacyService != null)
		{
			privacyService.getProtected().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						encryptWithPassword(localCache, privacyService.getCryptoPad());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});


			privacyService.getProtectionRemoved().add(new EventListenerEx<EventArgs>() {

				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					try {
						removeEncryption( localCache, privacyService.getCryptoPad());
					} catch (AuthenticationRequieredException e) {
						e.printStackTrace();
					}
				}
			});
		}else
			System.err.println("CacheKeyProvider: IPrivacyService is not avaiable!");
	}


	@Override
	public byte[] getEncryptionKeyPad(CryptedImageFileCache cache) {
		return getEncryptionKeyPad(cache.getCacheRoot());
	}


	/**
	 * Gets the encryption keypad for the given folder
	 * @param root
	 * @return
	 */
	public byte[] getEncryptionKeyPad(DirectoiryLocation root) {

		byte[] key = defaultKeyPad;

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return key;
		}

		if(keyFile.exists())
		{
			InputStream iStreamkeyFile = keyFile.openInputStream();
			try {
				try {
					key = FileSupport.readAllBytes(iStreamkeyFile, (int)keyFile.length());
					KeyPad.checkKey(key);
				} catch (KeyCurruptedException e) {
					e.printStackTrace();
				}finally{
					iStreamkeyFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}


		}else if(enckeyFile.exists()){

			try {
				byte[] enckey = enckeyFile.readAllBytes();
				byte[] secret = privacyService.getCryptoPad();
				key = keyCrypter.deCrypt(enckey, secret);

				KeyPad.checkKey(key);
			} catch (KeyCurruptedException e) {
				e.printStackTrace();
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else{
			// no file found
			// time to generate a new key

			key = KeyPad.generateKey(KEYPAD_SIZE);
			try {
				keyFile.writeAllBytes(key);
				System.out.println("generated cache encryption key");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return key;
	}






	/**
	 * Encrypt the key pad with the given password
	 * 
	 * 
	 * @param root
	 * @param password
	 */
	private void encryptWithPassword(DirectoiryLocation root, byte[] password){

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);

			if(enckeyFile.exists())
				throw new NotSupportedException("Already encrypted!");

			if(!keyFile.exists())
				throw new NotSupportedException("Keyfile not found! " + keyFile);

			try {
				byte[] key = keyFile.readAllBytes();
				byte[] encKey = keyCrypter.enCrypt(key, password);

				enckeyFile.writeAllBytes(encKey);
				keyFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}



	/**
	 * Removes the encryption from the key pad with the given password
	 *
	 * @param root
	 * @param password
	 */
	private void removeEncryption(DirectoiryLocation root, byte[] oldPass){

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);

			if(enckeyFile.exists()){

				try {
					byte[] encKey = enckeyFile.readAllBytes();
					byte[] key = keyCrypter.deCrypt(encKey, oldPass);

					keyFile.delete();
					keyFile.writeAllBytes(key);
					enckeyFile.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}else
				throw new NotSupportedException("Already decrypted!");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

	}





}
