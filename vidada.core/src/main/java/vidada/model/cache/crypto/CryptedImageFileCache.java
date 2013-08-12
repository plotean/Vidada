package vidada.model.cache.crypto;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;

import vidada.model.cache.ImageFileCache;
import archimedesJ.crypto.IByteBufferEncryption;
import archimedesJ.crypto.XORByteCrypter;
import archimedesJ.geometry.Size;
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

		ImageIO.setUseCache(false);
	}


	@Override
	protected BufferedImage load(File path){
		//
		// directly load the image
		//
		return loadfromEnCryptedFile(path);
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
	 * Loads the given file into memory, decrypts the image and creates a BufferedImage
	 * This method may take a while depending on the image size and crypto performance
	 * 
	 * @param path
	 * @return
	 */
	protected BufferedImage loadfromEnCryptedFile(File path){
		BufferedImage image = null;
		FileInputStream fis = null;
		ByteArrayInputStream proxyStream = null;
		byte[] buffer = null;


		try {
			//read the file into a byte array
			fis = new FileInputStream(path);
			buffer = new byte[(int) path.length()];
			fis.read(buffer);

			//decrypt the byte array
			buffer = bytestreamEncrypter.deCrypt(buffer, getEncryptionKeyPad());
			proxyStream = new ByteArrayInputStream(buffer);
			image = ImageIO.read(proxyStream);

		}catch(IOException e){
			e.printStackTrace();
		} finally {
			try {
				if(fis != null) fis.close();
				if(proxyStream != null) { proxyStream.close(); }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	@Override
	public BufferedImage getNativeImage(String id){
		BufferedImage image = super.getNativeImage(id);
		File metaInfo = getMetaInfoPath(id);
		if(!metaInfo.exists())
			storeNativeImageResolution(id, new Dimension(image.getWidth(), image.getHeight()));
		return image;
	}


	@Override
	public void storeNativeImage(String id, BufferedImage image){
		super.storeNativeImage(id, image);
		storeNativeImageResolution(id, new Dimension(image.getWidth(), image.getHeight()));
	}

	@Override
	protected void persist(BufferedImage image, File path){

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		FileOutputStream fos = null;

		try {
			ImageIO.write(image, "png", out);
			byte[] encodedImage = out.toByteArray();
			byte[] cryptedImageData = bytestreamEncrypter.enCrypt(encodedImage, getEncryptionKeyPad());

			path.getParentFile().mkdirs();
			fos = new FileOutputStream(path);
			fos.write(cryptedImageData);

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if(out!=null) out.close();
				if(fos!=null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void storeNativeImageResolution(String id, Dimension resolution){
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
