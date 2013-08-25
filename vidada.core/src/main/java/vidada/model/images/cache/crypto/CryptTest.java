package vidada.model.images.cache.crypto;

import archimedesJ.crypto.KeyCurruptedException;
import archimedesJ.crypto.KeyPad;
import archimedesJ.security.Credentials;
import archimedesJ.util.Debug;

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
