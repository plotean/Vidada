package vidada.model.images.cache.crypto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import vidada.model.images.cache.ImageFileCache;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;
import archimedesJ.util.FileSupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	public CryptedImageFileCache(ICacheKeyProvider keyProvider){
		this(new XORByteCrypter(), keyProvider);
	}

	/**
	 * Creates a encrypted image file cache with the default encryption algorithm
	 * 
	 * @param encryption Buffer encryption strategy
	 * @param keyProvider Cache keypad provider
	 */
	public CryptedImageFileCache(IByteBufferEncryption encryption,  ICacheKeyProvider keyProvider){
		bytestreamEncrypter = encryption;
		cachekeyProvider = keyProvider;
	}


	@Override
	protected InputStream openImageStream(File path){

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



	@Override
	public IMemoryImage getNativeImage(String id){
		IMemoryImage image = super.getNativeImage(id);
		File metaInfo = getMetaInfoPath(id);
		if(!metaInfo.exists())
			storeNativeImageResolution(id, new Size(image.getWidth(), image.getHeight()));
		return image;
	}


	@Override
	public void storeNativeImage(String id, IMemoryImage image){
		super.storeNativeImage(id, image);
		storeNativeImageResolution(id, new Size(image.getWidth(), image.getHeight()));
	}


	/**
	 * Encrypt the bytes 
	 */
	@Override
	protected byte[] retrieveBytes(IMemoryImage image){
		byte[] encodedImage = super.retrieveBytes(image);
		return bytestreamEncrypter.enCrypt(encodedImage, getEncryptionKeyPad());
	}


	private void storeNativeImageResolution(String id, Size resolution){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(resolution);
		try {
			FileSupport.writeToFile(getMetaInfoPath(id), jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the meta info file path for the given id
	 * @param id
	 * @return
	 */
	private File getMetaInfoPath(String id){
		return new File(getFilePathNative(id).getAbsolutePath() + ".json");
	}

	/**
	 * Get the native image resolution
	 * This implementation gets it from the meta information
	 */
	@Override
	public Size getNativeImageResolution(String id) {
		Size dimension = null;

		File metaFile = getMetaInfoPath(id);
		if(metaFile.exists()){
			try {
				String metaInfoJson = FileSupport.readFileToString(metaFile);
				Gson gson = new GsonBuilder().create();
				dimension = gson.fromJson(metaInfoJson, Size.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dimension;
	}
}
