package vidada.model.images.cache;


import archimedes.core.data.BiTuple;
import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;
import archimedes.core.images.IRawImageFactory;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.ResourceLocation;
import org.apache.commons.io.output.ByteArrayOutputStream;
import vidada.services.ServiceProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.*;


/**
 * A basic implementation of a image file cache, 
 * using the current OS file system to persist and manage the images.
 * 
 * @author IsNull
 *
 */
public class ImageFileCache implements IImageCache {

	private static final String RESOLUTION_DELEMITER = "_";

	private final DirectoryLocation cacheRoot;
	private final DirectoryLocation scaledCacheDataBase;

	private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

	// file path caches
	private final Map<Integer, ResourceLocation> dimensionPathCache = new HashMap<Integer, ResourceLocation>(2000);
	private final Map<Size, DirectoryLocation> resolutionFolders = new HashMap<Size, DirectoryLocation>(10);



	private final Set<Size> knownDimensions = new HashSet<Size>();
	private final Object knownDimensionsLOCK = new Object();

	/**
	 * Creates a new image cache which uses the file system to store images.
	 * 
	 * This is a basic implementation of the image file cache, 
	 * using the current OS file system to persist and manage the images.
	 * 
	 * @param cacheRoot The root folder for the cache
	 */
	public ImageFileCache(DirectoryLocation cacheRoot){

		this.cacheRoot = cacheRoot;
		scaledCacheDataBase = getScaledCache(cacheRoot);
		scaledCacheDataBase.mkdirs();

		//
		// initially, read the existing scaled resolutions
		//
		List<DirectoryLocation> resolutionFolders = scaledCacheDataBase.listDirs();

		if(resolutionFolders != null && !resolutionFolders.isEmpty())
		{
			for (DirectoryLocation folder : resolutionFolders) {
				String[] parts  = folder.getName().split(RESOLUTION_DELEMITER);
				if(parts.length == 2){
					try {
						knownDimensions.add(new Size(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
					} catch (NumberFormatException e) {
						// Ignore wrong (NON resolution) folders
					}
				}
			}
		}
	}



	public DirectoryLocation getCacheRoot() {
		return cacheRoot;
	}

	private static DirectoryLocation getScaledCache(DirectoryLocation cacheRoot){
		try {
			return DirectoryLocation.Factory.create(cacheRoot, "scaled");
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		return null;
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


		ResourceLocation cachedPath = getFilePath(id, size);
		if(cachedPath.exists())
		{
			try {
				thumbnail = load(cachedPath); 
			}catch(Exception e) {
				System.err.println("Can not read image" + cachedPath);
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
		ResourceLocation outputfile = getFilePath(id, imageSize);

		persist(image, outputfile);

		synchronized (knownDimensionsLOCK) {
			knownDimensions.add(imageSize);
		}
	}




	@Override
	public boolean exists(String id, Size size) {
		ResourceLocation cachedPath = getFilePath(id, size);
		//
		// The appropriate thing here would be Files.isReadable( cachedPath );
		// However, isReadable seems to have a very big performance hit when called in frequent rounds.

		//System.out.println("ImageFileCache.exist-> path: "  + cachedPath);

		return cachedPath.exists();
	}



	@Override
	public void removeImage(String id) {

		ResourceLocation path;
		for (Size knownDim : getKnownDimensions()) 
		{
			path = getFilePath(id, knownDim);
			path.delete();
		}
	}

	/**
	 * Load an image file from the disk
	 * @param path
	 * @return
	 */
	protected final IMemoryImage load(ResourceLocation path){
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
	protected InputStream openImageStream(ResourceLocation path){
		return path.openInputStream();
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
	protected final void persist(IMemoryImage image, ResourceLocation path){
		byte[] rawImageData = retrieveBytes(image);

		path.mkdirs();

		try {
			OutputStream fos =  path.openOutputStream();
			if(fos == null) return;
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
	protected ResourceLocation getFilePath(String id, Size size){

		int combindedHash = BiTuple.hashCode(id, size);

		ResourceLocation cachedThumb = dimensionPathCache.get(combindedHash);

		if(cachedThumb == null)
		{
			DirectoryLocation folder = getFolderForResolution( size );
			try {
				cachedThumb = ResourceLocation.Factory.create(folder, id + getImageExtension());
				dimensionPathCache.put(combindedHash, cachedThumb); 
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		return cachedThumb;
	}

	public String getImageExtension(){
		return ".png";
	}


	private DirectoryLocation getFolderForResolution(Size size){

		DirectoryLocation resolutionFolder = resolutionFolders.get(size);

		if(resolutionFolder == null){

			String resolutionName = size.width + RESOLUTION_DELEMITER + size.height;

			try {
				resolutionFolder = DirectoryLocation.Factory
						.create(scaledCacheDataBase, resolutionName);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			resolutionFolders.put(size, resolutionFolder);
		}

		return resolutionFolder;
	}






}
