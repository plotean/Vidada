package vidada.model.cache;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import vidada.model.settings.GlobalSettings;
import archimedesJ.data.BiTuple;
import archimedesJ.geometry.Size;
import archimedesJ.util.images.ImageInfo;
import archimedesJ.util.images.SimpleImageInfo;

/**
 * A basic implementation of the image file cache, 
 * using the current OS file system to persist and manage the images
 * 
 * @author IsNull
 *
 */
public class ImageFileCache implements IImageCacheService {

	private static final String RESOLUTION_DELEMITER = "_";


	private final File scaledCacheDataBase;
	private final File nativeCacheDataBase;

	// file path caches
	private final Map<Integer, File> dimensionPathCache = new HashMap<Integer, File>(2000);
	private final Map<Integer, File> nativePathCache = new HashMap<Integer, File>(2000);
	private final Map<Size, File> resolutionFolders = new HashMap<Size, File>(10);

	private final Map<String, Boolean> nativeImageExistCache = new HashMap<String, Boolean>(2000);


	private final Set<Size> knownDimensions = new HashSet<Size>();
	private final Object knownDimensionsLOCK = new Object();

	/**
	 * Creates a new ImageFileCache.
	 * This is a basic implementation of the image file cache, 
	 * using the current OS file system to persist and manage the images
	 */
	public ImageFileCache(){	

		File cacheDataBase = getCacheRoot(); // new File(decodedPath, "cache");
		scaledCacheDataBase = new File(cacheDataBase, "scaled");
		nativeCacheDataBase = new File(cacheDataBase, "native");

		scaledCacheDataBase.mkdirs();
		nativeCacheDataBase.mkdirs();

		System.out.println("image cache located at: " + scaledCacheDataBase.getAbsolutePath());

		//
		// initially, read the existing scaled resolutions
		//
		File[] resolutionFolders = scaledCacheDataBase.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});


		if(resolutionFolders != null)
		{
			for (File folder : resolutionFolders) {
				String[] parts  = folder.getName().split(RESOLUTION_DELEMITER);
				if(parts.length == 2){
					try {
						knownDimensions.add(new Size(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
					} catch (NumberFormatException e) {
						// igonre wrong folders
					}

				}
			}
		}
	}


	/**
	 * Gets the root folder of this file cache
	 * @return
	 */
	public static File getCacheRoot(){
		return GlobalSettings.getInstance().getAbsoluteCachePath();
	}



	/**
	 * Returns all cached image dimensions for the given id.
	 */
	@Override
	public Set<Size> getCachedDimensions(String id) {

		Set<Size> avaiableForId = new HashSet<Size>();

		for (Size knownDim : getKnownDimensions()) 
		{
			if(getFilePath( id, knownDim ).exists())
			{
				avaiableForId.add(knownDim);
			}
		}

		return avaiableForId;
	}


	protected Set<Size> getKnownDimensions(){
		synchronized (knownDimensionsLOCK) {
			return new HashSet<Size>(this.knownDimensions);
		}
	}


	@Override
	public Image getImageById(String id, Size size) {

		Image thumbnail = null;

		File cachedPath = getFilePath(id, size);
		if(cachedPath.exists())
		{
			try {

				thumbnail = load(cachedPath); 
			}catch(Exception e) {
				System.err.println("Can not read image" + cachedPath.getAbsolutePath());
			}
		}

		return thumbnail;
	}




	/**
	 * Returns the native image by its id
	 * @param id
	 * @return
	 */
	@Override
	public BufferedImage getNativeImage(String id){
		BufferedImage nativeImage = null;

		File cachedPath = getFilePathNative(id);
		if(cachedPath.exists())
		{
			try {
				nativeImage = load(cachedPath);
			}catch(Exception e) {
				System.err.println("Can not read native image" + cachedPath.getAbsolutePath());
			}
		}
		return nativeImage;	
	}


	/**
	 * Store a new image. It will be handled as the new native image.
	 */
	@Override
	public void storeNativeImage(String id, BufferedImage image) {

		if(image == null)
			throw new IllegalArgumentException("image can not be null");

		File outputfile = getFilePathNative(id);
		persist(image, outputfile);	    

		nativeImageExistCache.put(id, true);
	}


	/**
	 * Store a new image. It will be handled as the new native image.
	 */
	@Override
	public void storeImage(String id, BufferedImage image) {

		if(image == null)
			throw new IllegalArgumentException("image can not be null");


		Size imageSize = new Size(image.getWidth(), image.getHeight());
		File outputfile = getFilePath(id, imageSize);

		persist(image, outputfile);

		synchronized (knownDimensionsLOCK) {
			knownDimensions.add(imageSize);
		}
	}




	@Override
	public boolean exists(String id, Size size) {
		File cachedPath = getFilePath(id, size);
		//
		// The appropriate thing here would be Files.isReadable( cachedPath );
		// However, isReadable seems to have a very big performance hit when called in frequent rounds.

		//System.out.println("ImageFileCache.exist-> path: "  + cachedPath);

		return cachedPath.exists();
	}



	/**
	 * Checks if the native image exists
	 */
	@Override
	public boolean nativeImageExists(String id){

		if(!nativeImageExistCache.containsKey(id))
		{
			File cachedPath = getFilePathNative(id);
			nativeImageExistCache.put(id, cachedPath.exists());
		}

		return nativeImageExistCache.get(id);
	}



	@Override
	public void removeImage(String id) {

		FileUtils.deleteQuietly(getFilePathNative(id));

		nativeImageExistCache.put(id, false);

		File path;
		for (Size knownDim : getKnownDimensions()) 
		{
			path = getFilePath( id, knownDim );
			FileUtils.deleteQuietly(path);
		}
	}

	/**
	 * Load an image file from the disk
	 * @param path
	 * @return
	 */
	protected BufferedImage load(File path){
		BufferedImage image = null;
		try {
			image = ImageIO.read(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Writes the given image to disk
	 * @param image
	 * @param path
	 */
	protected void persist(BufferedImage image, File path){
		try {

			path.mkdirs();

			ImageIO.write(image, "png", path);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Gets the filepath for the given image id in the given resolution
	 * @param id
	 * @param size
	 * @return
	 */
	protected File getFilePath(String id, Size size){

		int combindedHash = BiTuple.hashCode(id, size);

		if(!dimensionPathCache.containsKey(combindedHash))
		{
			dimensionPathCache.put(combindedHash, new File(getFolderForResolution( size ), id + getImageExtension()));
		}

		return  dimensionPathCache.get(combindedHash);
	}

	/**
	 * Returns the native image resolution as long as the native image exists
	 */
	@Override
	public Size getNativeImageResolution(String id) {

		Size nativeResolution = null;

		File imageFile = getFilePathNative(id);
		if(imageFile.exists())
		{
			try {
				ImageInfo info = SimpleImageInfo.parseInfo(imageFile);
				if(info != null && info.isValid())
					nativeResolution = new Size(info.getWidth(), info.getHeight());
				else
					System.err.println("extracted info INVALID: " + info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return nativeResolution;
	}

	public String getImageExtension(){
		return ".png";
	}


	/**
	 * Gets the filepath to the native image
	 * @param id
	 * @return
	 */
	protected File getFilePathNative(String id){
		int combindedHash = id.hashCode();

		if(!nativePathCache.containsKey(combindedHash))
		{
			nativePathCache.put(combindedHash, new File(nativeCacheDataBase, id + getImageExtension()));
		}

		return nativePathCache.get(combindedHash);
	}



	private File getFolderForResolution(Size size){
		if(!resolutionFolders.containsKey(size)){
			resolutionFolders.put(size, new File(scaledCacheDataBase, size.width + RESOLUTION_DELEMITER + size.height));
		}

		return resolutionFolders.get(size);
	}





}
