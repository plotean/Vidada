package vidada.model.images.cache.crypto;

import java.io.File;
import java.io.IOException;

import vidada.model.ServiceProvider;
import vidada.model.images.cache.ImageFileCache;
import vidada.model.security.AuthenticationRequieredException;
import vidada.model.security.IPrivacyService;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.KeyCurruptedException;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.util.FileSupport;




/**
 * The cache encryption:
 * 
 * DECRYPTED STATE
 * 
 * In the NOT encrypted state, the encryption keypad is saved
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
	private final IByteBufferEncryption keyCrypter = new XORByteCrypter(); //new AESByteBufferCrypter();


	public VidadaCacheKeyProvider(){

		if(privacyService != null)
		{
			privacyService.getProtected().add(new EventListenerEx<EventArgsG<byte[]>>() {

				@Override
				public void eventOccured(Object sender, EventArgsG<byte[]> eventArgs) {
					encryptWithPassword(ImageFileCache.getCacheRoot(), eventArgs.getValue());
				}
			});


			privacyService.getProtectionRemoved().add(new EventListenerEx<EventArgsG<byte[]>>() {

				@Override
				public void eventOccured(Object sender, EventArgsG<byte[]> eventArgs) {
					removeEncryption(ImageFileCache.getCacheRoot(), eventArgs.getValue());
				}
			});
		}else
			System.err.println("CacheKeyProvider: IPrivacyService is not avaiable!");
	}




	@Override
	public byte[] getEncryptionKeyPad(CryptedImageFileCache cache) {
		return getEncryptionKeyPad(ImageFileCache.getCacheRoot());
	}


	/**
	 * Gets the encryption keypad for the given folder
	 * @param root
	 * @return
	 */
	public byte[] getEncryptionKeyPad(File root) {

		byte[] key = defaultKeyPad;

		File keyFile = new File(root, KeyFileName);
		File enckeyFile = new File(root, EncryptedKeyFileName);

		if(keyFile.exists())
		{
			try {
				key = FileSupport.readAllBytes(keyFile);
				KeyPad.checkKey(key);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeyCurruptedException e) {
				e.printStackTrace();
			}
		}else if(enckeyFile.exists()){

			try {
				byte[] enckey = FileSupport.readAllBytes(enckeyFile);
				byte[] secret = privacyService.getUserSecret();
				key = keyCrypter.deCrypt(enckey, secret);

				KeyPad.checkKey(key);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeyCurruptedException e) {
				e.printStackTrace();
			} catch (AuthenticationRequieredException e) {
				e.printStackTrace();
			}

		}else{
			// no file found
			// time to generate a new key

			key = KeyPad.generateKey(KEYPAD_SIZE);
			try {
				FileSupport.writeToFile(keyFile, key);
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
	private void encryptWithPassword(File root, byte[] secret){

		File keyFile = new File(root, KeyFileName);
		File enckeyFile = new File(root, EncryptedKeyFileName);

		if(enckeyFile.exists())
			throw new NotSupportedException("Already encrypted!");

		if(!keyFile.exists())
			throw new NotSupportedException("Keyfile not found! " + keyFile);

		try {
			byte[] key = FileSupport.readAllBytes(keyFile);
			byte[] encKey = keyCrypter.enCrypt(key, secret);

			FileSupport.writeToFile(enckeyFile, encKey);
			keyFile.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Removes the encryption from the key pad with the given password
	 *
	 * @param root
	 * @param password
	 */
	private void removeEncryption(File root, byte[] oldSecret){

		File keyFile = new File(root, KeyFileName);
		File enckeyFile = new File(root, EncryptedKeyFileName);

		if(enckeyFile.exists()){

			try {
				byte[] encKey = FileSupport.readAllBytes(enckeyFile);
				byte[] key = keyCrypter.deCrypt(encKey, oldSecret);

				keyFile.delete();
				FileSupport.writeToFile(keyFile, key);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}else
			throw new NotSupportedException("Already decrypted!");

	}





}
