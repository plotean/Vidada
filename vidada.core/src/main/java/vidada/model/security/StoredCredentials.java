package vidada.model.security;

import java.io.UnsupportedEncodingException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.security.Credentials;

/**
 * Represents a encrypted Credential-Set which can be safely persisted
 * @author IsNull
 *
 */
@Entity
public class StoredCredentials {

	transient private final static String StringEncoding = "utf-8";
	transient private final static IByteBufferEncryption credentialsCrypter = new XORByteCrypter();

	// persisted fields
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String authDomain;
	private byte[] encDomain;
	private byte[] encUser;
	private byte[] encPass;


	/**
	 * ORM Constructor
	 */
	StoredCredentials() { };

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
