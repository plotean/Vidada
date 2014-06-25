import archimedes.core.crypto.KeyCurruptedException;
import archimedes.core.crypto.KeyPad;
import archimedes.core.security.Credentials;
import archimedes.core.util.Debug;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.images.cache.crypto.CryptedCacheUtil;

public class CryptTest {

    private static final Logger logger = LogManager.getLogger(CryptTest.class.getName());


    public static void main(String[] args){

		byte[] keyPad = KeyPad.generateKey(20);
        logger.info("generated: " + Debug.toString(keyPad));

		byte[] encrypted = CryptedCacheUtil.encryptPad(keyPad, new Credentials(null, "123"));
        logger.info("encrypted: " + Debug.toString(encrypted));


		byte[] decryptedPad = CryptedCacheUtil.decryptPad(encrypted, new Credentials(null, "123"));

		try {
			KeyPad.checkKey(keyPad);
		} catch (KeyCurruptedException e) {
			e.printStackTrace();
		}
	}
}
