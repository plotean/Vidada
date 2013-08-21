package vidada.model.images.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import vidada.model.ServiceProvider;
import vidada.model.images.RawImageFactory;
import vidada.model.settings.GlobalSettings;
import archimedesJ.data.BiTuple;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

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

	private final RawImageFactory imageFactory = ServiceProvider.Resolve(RawImageFactory.class);

	// file path caches
	private final Map<Integer, File> dimensionPathCache = new HashMap<Integer, File>(2000);
	private final Map<Size, File> resolutionFolders = new HashMap<Size, File>(10);



	private final Set<Size> knownDimensions = new HashSet<Size>();
	private final Object knownDimensionsLOCK = new Object();

	/**
	 * Creates a new ImageFileCache.
	 * This is a basic implementation of the image file cache, 
	 * using the current OS file system to persist and manage the images
	 */
	public ImageFileCache(){

		File cacheDataBase = GlobalSettings.getInstance().getAbsoluteCachePath(); // new File(decodedPath, "cache");
		scaledCacheDataBase = new File(cacheDataBase, "scaled");

		scaledCacheDataBase.mkdirs();

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
	public IMemoryImage getImageById(String id, Size size) {

		IMemoryImage thumbnail = null;

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
	 * Store a new image. It will be handled as the new native image.
	 */
	@Override
	public void storeImage(String id, IMemoryImage image) {

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



	@Override
	public void removeImage(String id) {

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
	protected final IMemoryImage load(File path){
		InputStream is = openImageStream(path);
		try{
			return imageFactory.createImage(is);
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Open a InputStream which must return the raw image bytes
	 * @param path
	 * @return
	 */
	protected InputStream openImageStream(File path){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return fis;
	}

	protected byte[] retrieveBytes(IMemoryImage image){
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024); 
		imageFactory.writeImage(image, out);
		return out.toByteArray();
		// no need for byte array stream to be closed explicitly
	}


	/**
	 * Writes the given image to disk
	 * @param image
	 * @param path
	 */
	protected final void persist(IMemoryImage image, File path){
		byte[] rawImageData = retrieveBytes(image);
		path.getParentFile().mkdirs();

		try {
			FileOutputStream fos = new FileOutputStream(path);
			try{
				fos.write(rawImageData);
			}finally{
				if(fos != null)
					fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

	public String getImageExtension(){
		return ".png";
	}


	private File getFolderForResolution(Size size){
		if(!resolutionFolders.containsKey(size)){
			resolutionFolders.put(size, new File(scaledCacheDataBase, size.width + RESOLUTION_DELEMITER + size.height));
		}

		return resolutionFolders.get(size);
	}





}
