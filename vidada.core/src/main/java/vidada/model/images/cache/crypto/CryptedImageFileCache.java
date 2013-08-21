package vidada.model.images.cache.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import vidada.model.images.cache.ImageFileCache;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.images.IMemoryImage;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Represents a encrypted image cache
 * @author IsNull
 *
 */
public class CryptedImageFileCache extends ImageFileCache {

	byte[] keypad = null;

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
	public CryptedImageFileCache(DirectoiryLocation cacheRoot, ICacheKeyProvider keyProvider){
		this(cacheRoot, new XORByteCrypter(), keyProvider);
	}

	/**
	 * Creates a encrypted image file cache with the default encryption algorithm
	 * 
	 * @param encryption Buffer encryption strategy
	 * @param keyProvider Cache keypad provider
	 */
	public CryptedImageFileCache(DirectoiryLocation cacheRoot, IByteBufferEncryption encryption,  ICacheKeyProvider keyProvider){
		super(cacheRoot);
		bytestreamEncrypter = encryption;
		cachekeyProvider = keyProvider;
	}


	@Override
	protected InputStream openImageStream(ResourceLocation path){

		InputStream fis = super.openImageStream(path);
		// the file was stored encrypted, so we have to decrypt the inputstream

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
	protected byte[] getEncryptionKeyPad(){
		if(keypad == null)
		{
			keypad = cachekeyProvider.getEncryptionKeyPad(this);
		}
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
