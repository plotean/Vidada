package vidada.model.images.cache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import archimedesJ.data.caching.LRUCache;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Implements a basic memory image cache for super fast 
 * item access.
 * 
 * The cache uses now a LRU Cache to stay inside a max boundary
 * of memory consumption. Yet, the values are not dependent on
 * any system and VM configuration such as the available memory.
 * 
 *
 * 
 * @author IsNull
 *
 */
public class MemoryImageCache implements IImageCache {

	private final IImageCache unbufferedImageCache;
	private final Map<Size, Map<String, SoftReference<IMemoryImage>>> imageCache;

	/**
	 * Average scaled image size in MB
	 */
	private final static float AVERAGE_SCALED_IMAGE_SIZE = 0.7f;


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
	private static final float MAX_OVERALL_MEMORY_SCALED_CACHE = 2000f; //2GB


	/**
	 * Max numbers of cached resolution size folders
	 */
	private static int MAX_SCALED_CACHE_SIZEFOLDERS = 5; 

	/**
	 * Max size of a single resolution cache
	 */
	public static final float MAX_MEMORY_SCALED_CACHE = MAX_OVERALL_MEMORY_SCALED_CACHE / MAX_SCALED_CACHE_SIZEFOLDERS;

	/**
	 * Wraps this memory cache around the given cache service,
	 * that means that images only once are requested from the 
	 * underlining cache. 
	 * (Or in case the memory cache exceeds its size limit)
	 * 
	 * @param unbufferedImageCache A slow cache which gets a memory cache wrapper
	 */
	public MemoryImageCache(IImageCache unbufferedImageCache){
		this.unbufferedImageCache = unbufferedImageCache;

		// Setup image cache
		this.imageCache = Collections.synchronizedMap(
				new LRUCache<Size, Map<String, SoftReference<IMemoryImage>>>(MAX_SCALED_CACHE_SIZEFOLDERS));
	}


	@Override
	public IMemoryImage getImageById(String id, Size size) {
		IMemoryImage image = null;

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
	public void storeImage(String id, IMemoryImage image) {
		this.unbufferedImageCache.storeImage(id, image);
		storeImageInMemoryCache(id, image);
	}

	@Override
	public void removeImage(String id) {

		for (Size d : new HashSet<Size>(imageCache.keySet())) {
			Map<String, SoftReference<IMemoryImage>> idImageMap = imageCache.get(d);
			if(idImageMap.containsKey(id))
			{
				idImageMap.remove(id);
			}

			if(idImageMap.isEmpty())
				imageCache.remove(d);
		}

		unbufferedImageCache.removeImage(id);
	}


	protected boolean existsInMemoryCache(String id, Size size){
		Map<String, SoftReference<IMemoryImage>> map = imageCache.get(size);
		if(map != null){
			SoftReference<IMemoryImage> imageRef = map.get(id);
			return imageRef != null ? imageRef.get() != null : false;
		}
		return false;	
	}


	protected void storeImageInMemoryCache(String id, IMemoryImage image) {
		if(image != null)
		{
			Size d = new Size(image.getWidth(), image.getHeight());
			storeImageInMemoryCache(id, image, d);
		}
	}


	protected void storeImageInMemoryCache(String id, IMemoryImage image, Size imageSize) {

		if(image != null)
		{
			if(!imageCache.containsKey(imageSize))
			{
				int maxScaledSize = (int)(MAX_MEMORY_SCALED_CACHE / AVERAGE_SCALED_IMAGE_SIZE);
				Map<String, SoftReference<IMemoryImage>> realImageCache = new LRUCache<String, SoftReference<IMemoryImage>>(maxScaledSize);
				imageCache.put(imageSize, realImageCache);

				System.out.println("Created: Scaled image cache. maxScaledSize: " + maxScaledSize);
			}
			imageCache.get(imageSize).put(id, new SoftReference<IMemoryImage>(image));
		}
	}



}
