package vidada.model.cache;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;

/**
 * Implements a basic memory image cache for super fast 
 * item access.
 * 
 * The cache uses now a LRU Cache to stay inside a max boundary
 * of memory consumption. Yet, the values are not dependent on
 * any system and VM configuration such as the available memory.
 * 
 * 
 * @author IsNull
 *
 */
public class MemoryImageCache implements IImageCacheService {

	private final IImageCacheService unbufferedImageCache;
	private final Map<Size, Map<String, SoftReference<Image>>> imageCache;
	private final Map<String, Size> nativeImageResolution;

	/**
	 * Average native image size in MB
	 * (We assume that there are some 720p/1080p movies...)
	 */
	private final static float AVERAGE_NATIVE_IMAGE_SIZE = 1.3f;

	/**
	 * Average scaled image size in MB
	 */
	private final static float AVERAGE_SCALED_IMAGE_SIZE = 0.7f;

	/**
	 * Maximum amount in MB the native image cache is allowed to take
	 */
	private final float MAX_MEMORY_NATIVE_CACHE = 250f; // 250 MB

	/**
	 * Maximum amount in MB the scaled image cache is allowed to take
	 * 
	 * This cache is much more important in the usage, as 
	 * generally the native image is only required to create
	 * the scaled thumbs and afterwards is no longer loaded. 
	 * Therefore this cache has a higher overall usage rate.
	 * 
	 * Each resolution has its own cache, so you need
	 * to divide the actual value by the number of caches you expect
	 */
	private final float MAX_OVERALL_MEMORY_SCALED_CACHE = 2000f; //2GB


	/**
	 * Max numbers of cached resolution size folders
	 */
	private int MAX_SCALED_CACHE_SIZEFOLDERS = 5; 

	/**
	 * Max size of a single resolution cache
	 */
	private final float MAX_MEMORY_SCALED_CACHE = MAX_OVERALL_MEMORY_SCALED_CACHE / MAX_SCALED_CACHE_SIZEFOLDERS;

	/**
	 * Native sized frame cache, which has a limited size.
	 * If we assume that a single frame takes 1.3MB space,
	 * the max size oft this cache can be calculated
	 * 
	 */
	private final Map<String, SoftReference<Image>> nativeImageCache;

	/**
	 * Wraps this memory cache around the given cache service,
	 * that means that images only once are requested from the 
	 * underlining cache. 
	 * (Or in case the memory cache exceeds its size limit)
	 * 
	 * @param unbufferedImageCache A slow cache which gets a memory cache wrapper
	 */
	public MemoryImageCache(IImageCacheService unbufferedImageCache){
		this.unbufferedImageCache = unbufferedImageCache;

		// setup native image cache
		nativeImageResolution = new HashMap<String, Size>(300);
		int nativeCacheMaxSize = (int)(MAX_MEMORY_NATIVE_CACHE / AVERAGE_NATIVE_IMAGE_SIZE);
		this.nativeImageCache = Collections.synchronizedMap(new LRUCache<String, SoftReference<Image>>(nativeCacheMaxSize));

		System.out.println("Created: Native image cache. MAX Capacity: " + nativeCacheMaxSize);


		// setup resized image cache
		this.imageCache = Collections.synchronizedMap(
				new LRUCache<Size, Map<String, SoftReference<Image>>>(MAX_SCALED_CACHE_SIZEFOLDERS));
	}


	@Override
	public Image getImageById(String id, Size size) {
		Image image = null;

		if(existsInMemoryCache(id, size))
		{
			image = imageCache.get(size).get(id).get();
		} else {		
			image = this.unbufferedImageCache.getImageById(id, size);	
			storeImageInMemoryCache(id, image, size);
		}
		return image;
	}

	@Override
	public Set<Size> getCachedDimensions(String id) {
		return this.unbufferedImageCache.getCachedDimensions(id);
	}

	@Override
	public boolean exists(String id, Size size) {
		return existsInMemoryCache(id, size) || unbufferedImageCache.exists(id, size);
	}

	@Override
	public void storeImage(String id, BufferedImage image) {
		this.unbufferedImageCache.storeImage(id, image);
		storeImageInMemoryCache(id, image);
	}

	@Override
	public void removeImage(String id) {

		for (Size d : new HashSet<Size>(imageCache.keySet())) {
			Map<String, SoftReference<Image>> idImageMap = imageCache.get(d);
			if(idImageMap.containsKey(id))
			{
				idImageMap.remove(id);
			}

			if(idImageMap.isEmpty())
				imageCache.remove(d);
		}

		nativeImageCache.remove(id);

		unbufferedImageCache.removeImage(id);
	}


	protected boolean existsInMemoryCache(String id, Size size){
		Map<String, SoftReference<Image>> map = imageCache.get(size);
		if(map != null){
			SoftReference<Image> imageRef = map.get(id);
			return imageRef != null ? imageRef.get() != null : false;
		}
		return false;	
	}


	protected void storeImageInMemoryCache(String id, BufferedImage image) {
		if(image != null)
		{
			Size d = new Size(image.getWidth(), image.getHeight());
			storeImageInMemoryCache(id, image, d);
		}
	}


	protected void storeImageInMemoryCache(String id, Image image, Size imageSize) {

		if(image != null)
		{
			if(!imageCache.containsKey(imageSize))
			{
				int maxScaledSize = (int)(MAX_MEMORY_SCALED_CACHE / AVERAGE_SCALED_IMAGE_SIZE);
				Map<String, SoftReference<Image>> realImageCache = new LRUCache<String, SoftReference<Image>>(maxScaledSize);
				imageCache.put(imageSize, realImageCache);

				System.out.println("Created: Scaled image cache. MAX Capacity: " + maxScaledSize);
			}
			imageCache.get(imageSize).put(id, new SoftReference<Image>(image));
		}
	}


	@Override
	public void storeNativeImage(String id, BufferedImage image) {
		nativeImageCache.put(id, new SoftReference<Image>(image));

		this.unbufferedImageCache.storeNativeImage(id, image);
	}


	@Override
	public boolean nativeImageExists(String id) {
		SoftReference<Image> ref = nativeImageCache.get(id);
		return (ref != null && ref.get() != null) || this.unbufferedImageCache.nativeImageExists(id);
	}


	@Override
	public Image getNativeImage(String id) {

		SoftReference<Image> nativeImageRef = nativeImageCache.get(id);
		Image nativeImage = nativeImageRef != null ? nativeImageRef.get() : null;

		if(nativeImage != null)
		{
			nativeImage = this.unbufferedImageCache.getNativeImage(id);
			if(nativeImage != null)
				nativeImageCache.put(id, new SoftReference<Image>(nativeImage));
		}
		return nativeImage;
	}


	@Override
	public Size getNativeImageResolution(String id) {
		if(!nativeImageResolution.containsKey(id))
		{
			Size resolution = unbufferedImageCache.getNativeImageResolution(id);
			nativeImageResolution.put(id, resolution);
		}
		return nativeImageResolution.get(id);
	}

}
