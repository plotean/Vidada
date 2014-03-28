package vidada.model.images.cache.crypto;

import archimedes.core.crypto.IByteBufferEncryption;
import archimedes.core.crypto.KeyCurruptedException;
import archimedes.core.crypto.KeyPad;
import archimedes.core.crypto.XORByteCrypter;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.security.CredentialType;
import archimedes.core.security.Credentials;
import archimedes.core.util.Debug;
import vidada.model.security.CredentialUtil;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;

import java.io.IOException;
import java.net.URISyntaxException;

public class CryptedCacheUtil {

	transient private final static int KEYPAD_SIZE = 20;

	transient private final static IByteBufferEncryption keyCrypter = new XORByteCrypter();
	transient private final static String KeyFileName = "cache.keypad";
	transient private final static String EncryptedKeyFileName = "cache.encrypted";


	/**
	 * Gets the encryption keypad for the given folder
	 * @param root
	 * @return
	 */
	public static byte[] getEncryptionKeyPad(DirectoryLocation root, ICredentialManager credentialManager) {

		byte[] keyPad = null;

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return keyPad;
		}

		if(keyFile.exists())
		{
			System.out.println("Reading KeyPad from file: " + keyFile);
			keyPad = readKeyPad(keyFile);
		}else if(enckeyFile.exists()){
			System.out.println("Reading encrypted KeyPad from file: " + enckeyFile);
			keyPad = readEncryptedKeyPad(enckeyFile, credentialManager);
		}else{
			// no file found
			// time to generate a new key
			keyPad = generateNewKeyPad(keyFile);

		}
		return keyPad;
	}

	private static byte[] readKeyPad(ResourceLocation keyFile){
		byte[] keyPad = null;
		try {
			keyPad = keyFile.readAllBytes();
			KeyPad.checkKey(keyPad);
		} catch (KeyCurruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyPad;
	}

	/*
	private static byte[] readEncryptedKeyPad(ResourceLocation enckeyFile, Credentials credentials){
		byte[] keyPad = null;
		try {

			final byte[] encryptedPad = enckeyFile.readAllBytes();
			keyPad = decryptPad(encryptedPad, credentials);

			try {
				// should always pass this check 
				// since the creditals were checked previously
				KeyPad.checkKey(keyPad);
			} catch (KeyCurruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return keyPad;
	}
	 */

	private static byte[] readEncryptedKeyPad(ResourceLocation enckeyFile, ICredentialManager credentialManager){
		byte[] keyPad = null;

		try {

			final byte[] encryptedPad = enckeyFile.readAllBytes();
			System.out.println("CRYPTO PAD: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

			String domain = CredentialUtil.toDomain("vidada.cache", enckeyFile.toString());

			Credentials validCredentials = credentialManager.requestAuthentication(
					domain,
					"Enter password for cache " + enckeyFile.toString(),
					CredentialType.PasswordOnly,
					new CredentialsChecker() {
						@Override
						public boolean check(Credentials credentials) {

							byte[] currentKeyPad = decryptPad(encryptedPad, credentials);

							System.out.println("decrypted pad: " + Debug.toString(currentKeyPad) + " len: " + currentKeyPad.length );
							System.out.println("encrypted pad: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

							return KeyPad.validate(currentKeyPad);
						}
					}  ,true);

			if(validCredentials != null){
				System.out.println("ReadEncryptedKeyPad: Valid Credentials: " + validCredentials);

				keyPad = decryptPad(encryptedPad, validCredentials);

				System.out.println("decrypted pad: " + Debug.toString(keyPad) + " len: " + keyPad.length );
				System.out.println("encrypted pad: " + Debug.toString(encryptedPad) + " len: " + encryptedPad.length );

				try {
					// should always pass this check 
					// since the creditals were checked previously
					KeyPad.checkKey(keyPad);
				} catch (KeyCurruptedException e) {
					e.printStackTrace();
					keyPad = null;
				}
			}else {
				System.err.println("ReadEncryptedKeyPad: User could not provide correct credentials.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return keyPad;
	}


	private static byte[] generateNewKeyPad(ResourceLocation keyFile){
		byte[] keyPad = KeyPad.generateKey(KEYPAD_SIZE);
		try {
			keyFile.writeAllBytes(keyPad);
			System.out.println("generated cache encryption key");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyPad;
	}

	/**
	 * Encrypt the key pad with the given password
	 * 
	 * 
	 * @param root
	 * @param password
	 */
	public static void encryptWithPassword(DirectoryLocation root, Credentials credentials){

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
				byte[] pad = keyFile.readAllBytes();

				byte[] encPad = encryptPad(pad, credentials);

				enckeyFile.writeAllBytes(encPad);

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
	public static void removeEncryption(DirectoryLocation root, Credentials oldPass){

		ResourceLocation keyFile;
		ResourceLocation enckeyFile;
		try {
			keyFile = ResourceLocation.Factory.create(root, KeyFileName);
			enckeyFile = ResourceLocation.Factory.create(root, EncryptedKeyFileName);

			if(enckeyFile.exists()){

				try {
					byte[] encryptedPad = enckeyFile.readAllBytes();
					byte[] key = decryptPad(encryptedPad, oldPass);
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

	public static byte[] encryptPad(byte[] pad, Credentials credentials){
		return keyCrypter.enCrypt(pad, credentials.getUserSecret());
	}

	public static byte[] decryptPad(byte[] encryptedPad, Credentials credentials){
		return keyCrypter.deCrypt(encryptedPad, credentials.getUserSecret());
	}


}
