package vidada.model.images.cache.crypto;


import archimedes.core.crypto.IByteBufferEncryption;
import archimedes.core.crypto.XORByteCrypter;
import archimedes.core.images.IMemoryImage;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.security.AuthenticationException;
import vidada.model.images.cache.ImageFileCache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a encrypted image cache
 * @author IsNull
 *
 */
public class CryptedImageFileCache extends ImageFileCache {

	private byte[] keypad = null;
	private final Object keypadLock = new Object();

	private final IByteBufferEncryption bytestreamEncrypter;
	private final ICacheKeyProvider cachekeyProvider;


	/**
	 * Creates a encrypted image file cache with the default encryption algorithm
	 * 
	 * NOTE: The default algorithm is a simple XOR encryption which is very fast
	 * but provides bad security when the key is short
	 * 
	 * @param keyProvider Cache keypad provider
	 */
	public CryptedImageFileCache(DirectoryLocation cacheRoot, ICacheKeyProvider keyProvider) throws AuthenticationException{
		this(cacheRoot, new XORByteCrypter(), keyProvider);
	}

	/**
	 * Creates a encrypted image file cache with the default encryption algorithm
	 * 
	 * @param encryption Buffer encryption strategy
	 * @param keyProvider Cache keypad provider
	 */
	protected CryptedImageFileCache(DirectoryLocation cacheRoot,
			IByteBufferEncryption encryption,  ICacheKeyProvider keyProvider)throws AuthenticationException{
		super(cacheRoot);

		System.err.println("Creating CryptedImageFileCache!");

		bytestreamEncrypter = encryption;
		cachekeyProvider = keyProvider;


		keypad = cachekeyProvider.getEncryptionKeyPad(this);
		if(keypad == null){
			System.err.println("CryptedImageFileCache: cachekeyProvider returned a NULL EncryptionKeyPad!");
			throw new AuthenticationException();
		}else {
			System.out.println("CryptedImageFileCache: Got EncryptionKeyPad.");
		}
	}


	@Override
	protected InputStream openImageStream(ResourceLocation path){

		InputStream fis = super.openImageStream(path);
		// The file was stored encrypted, so we have to decrypt the InputStream

		ByteArrayInputStream proxyStream = null;
		byte[] buffer = null;


		try {
			//read the file into a byte array
			buffer = new byte[(int) path.length()];
			fis.read(buffer);

			// decrypt the byte array
			buffer = bytestreamEncrypter.deCrypt(buffer, getEncryptionKeyPad());

			// create a new InputStream, this time with the original image data
			proxyStream = new ByteArrayInputStream(buffer);

		}catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return proxyStream;
	} 


	/**
	 * Gets the key for the encrypt and decrypt cache items
	 * @return
	 */
	private byte[] getEncryptionKeyPad(){
		return keypad;
	}

	@Override
	public String getImageExtension(){
		return ".dat";
	}


	/**
	 * Encrypt the bytes 
	 */
	@Override
	protected byte[] retrieveBytes(IMemoryImage image){
		byte[] encodedImage = super.retrieveBytes(image);
		return bytestreamEncrypter.enCrypt(encodedImage, getEncryptionKeyPad());
	}
}
