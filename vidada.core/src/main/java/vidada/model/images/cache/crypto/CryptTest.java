package vidada.model.images.cache.crypto;

import archimedes.core.crypto.KeyCurruptedException;
import archimedes.core.crypto.KeyPad;
import archimedes.core.security.Credentials;
import archimedes.core.util.Debug;

public class CryptTest {

	public static void main(String[] args){

		byte[] keyPad = KeyPad.generateKey(20);
		System.out.println("generated: " + Debug.toString(keyPad));

		byte[] encrypted = CryptedCacheUtil.encryptPad(keyPad, new Credentials(null, "123"));
		System.out.println("encrypted: " + Debug.toString(encrypted));


		byte[] decryptedPad = CryptedCacheUtil.decryptPad(encrypted, new Credentials(null, "123"));

		try {
			KeyPad.checkKey(keyPad);
		} catch (KeyCurruptedException e) {
			e.printStackTrace();
		}
	}
}
