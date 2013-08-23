package vidada.model.security;

import java.io.UnsupportedEncodingException;

import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.io.locations.Credentials;

/**
 * Represents a encrypted Credential-Set which can be safely persisted
 * @author IsNull
 *
 */
public class StoredCredentials {

	transient private final static String StringEncoding = "utf-8";
	transient private final static IByteBufferEncryption credentialsCrypter = new XORByteCrypter();

	// persisted fields
	private String authDomain;
	private byte[] encDomain;
	private byte[] encUser;
	private byte[] encPass;

	/**
	 * 
	 * @param credentials
	 * @param domain
	 * @param key
	 */
	public StoredCredentials(Credentials credentials, String domain, byte[] key) {
		setCredentials(credentials, key);
		this.authDomain = domain;
	}

	public Credentials getCredentials(byte[] key) {
		try {
			return new Credentials(
					new String(credentialsCrypter.deCrypt(encDomain, key),StringEncoding),
					new String(credentialsCrypter.deCrypt(encUser, key),StringEncoding),
					new String(credentialsCrypter.deCrypt(encPass, key),StringEncoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void setCredentials(Credentials credentials, byte[] key) {
		this.encDomain = credentialsCrypter.enCrypt(getBytes(credentials.getDomain()), key);
		this.encUser = credentialsCrypter.enCrypt(getBytes(credentials.getUsername()), key);
		this.encPass = credentialsCrypter.enCrypt(getBytes(credentials.getPassword()), key);
	}

	private static byte[] getBytes(String str){
		if(str != null){
			try {
				return str.getBytes(StringEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}

	public String getDomain() {
		return authDomain;
	}

	protected void setDomain(String domain) {
		this.authDomain = domain;
	}

	@Override
	public String toString(){
		return getDomain();
	}

}
