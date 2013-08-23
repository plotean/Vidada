package vidada.model.images.cache.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import vidada.model.security.CredentialUtil;
import vidada.model.security.ICredentialManager;
import vidada.model.security.ICredentialManager.CredentialsChecker;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.KeyCurruptedException;
import archimedesJ.crypto.KeyPad;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.io.locations.Credentials;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.util.FileSupport;

public class CryptedCacheUtil {

	private final static int KEYPAD_SIZE = 20;
	//private final static byte[] defaultKeyPad = new byte[]{12,34,65,23,45,23};

	private final static IByteBufferEncryption keyCrypter = new XORByteCrypter();
	private final static String KeyFileName = "cache.keypad";
	private final static String EncryptedKeyFileName = "cache.encrypted";


	/**
	 * Gets the encryption keypad for the given folder
	 * @param root
	 * @return
	 */
	public static byte[] getEncryptionKeyPad(DirectoiryLocation root, ICredentialManager credentialManager) {

		byte[] key = null;

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

				final byte[] enckey = enckeyFile.readAllBytes();

				String domain = CredentialUtil.toDomain("vidada.cache", root.toString());

				Credentials validCredentials = credentialManager.requestAuthentication(
						domain,
						"Enter password for cache " + root.toString(), new CredentialsChecker() {
							@Override
							public boolean check(Credentials credentials) {
								byte[] currentKey = keyCrypter.deCrypt(
										enckey,
										credentials.getUserSecret());
								return KeyPad.validate(currentKey);
							}
						}  ,true);

				key = keyCrypter.deCrypt(
						enckey,
						validCredentials.getUserSecret());

				try {
					// should always pass this check 
					// since the creditals were checked previously
					KeyPad.checkKey(key);
				} catch (KeyCurruptedException e) {
					e.printStackTrace();
				}
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
	public static void encryptWithPassword(DirectoiryLocation root, byte[] password){

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
	public static void removeEncryption(DirectoiryLocation root, byte[] oldPass){

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
