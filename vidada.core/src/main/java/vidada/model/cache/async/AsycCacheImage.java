package vidada.model.cache.async;

import java.awt.Image;
import java.awt.image.BufferedImage;

import vidada.model.cache.CacheUtils;
import vidada.model.cache.IImageCacheService;
import archimedesJ.swing.images.AsyncImage;
import archimedesJ.swing.images.IImageLoader;
import archimedesJ.util.images.Images;

/**
 * Represents a cache image which can be loaded async
 * 
 * @author IsNull
 *
 */
public class AsycCacheImage extends AsyncImage<CachedImageInfo> {

	private static IImageLoader<CachedImageInfo> imageFileLoader = new IImageLoader<CachedImageInfo>(){
		@Override
		public BufferedImage loadImage(CachedImageInfo imageIdenifier) {
			BufferedImage img = null;

			IImageCacheService imageCache = imageIdenifier.imageCache;

			if(imageCache.exists(imageIdenifier.id, imageIdenifier.dimension)){
				Image image = imageCache.getImageById(imageIdenifier.id, imageIdenifier.dimension);
				if(image != null)
					return Images.toBufferedImage(image, BufferedImage.TYPE_INT_ARGB);
			}

			img = CacheUtils.getRescaledInstance(imageIdenifier.imageCache, imageIdenifier.id, imageIdenifier.dimension);
			if(img != null)
				imageCache.storeImage(imageIdenifier.id, img);
			else
				System.err.println("AsycCacheImage native image missing.");

			return img;
		}
	};



	/**
	 * Create an image which is loaded  async from the given file, using the default image loader
	 * @param imageIdentifier
	 */
	public AsycCacheImage(CachedImageInfo imageIdentifier) {
		this(imageIdentifier, imageFileLoader);
	}

	/**
	 * Create an image which is loaded  async from the given file, with the given image loading strategy
	 * @param imageIdentifier
	 */
	private AsycCacheImage(CachedImageInfo imageIdentifier, IImageLoader<CachedImageInfo> imageLoader) {
		super(imageLoader, imageIdentifier);
	}

}
