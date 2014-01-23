package vidada.model.images;

import java.util.concurrent.Callable;

import vidada.model.images.cache.CacheUtils;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.settings.GlobalSettings;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Represents the logic of retrieving a well sized for a media item
 * @author IsNull
 *
 */
public class ImageLoaderTask implements Callable<IMemoryImage>
{
	transient private static final Size maxThumbSize = GlobalSettings.getInstance().getMaxThumbResolution();

	transient private final IThumbImageCreator thumbImageCreator = new ThumbImageExtractor();

	transient private final IImageCache imageCache;
	transient private final MediaItem media;
	transient private final Size size;


	public ImageLoaderTask(IImageCache imageCache, MediaItem media, Size size){
		this.imageCache = imageCache;
		this.media = media;
		this.size = size;
	}


	/**
	 * Load the requested image as memory image (bitmap)
	 */
	@Override
	public IMemoryImage call() throws Exception {

		IMemoryImage loadedImage = null;


		//Thread.sleep(100);  TODO:
		// the image may already be cached

		loadedImage = imageCache.getImageById(media.getFilehash(), size);

		if(loadedImage == null){
			// bad luck, the image does not exist in the cache

			// but we may already have cached a bigger version of the requested size
			// if so, we rescaled this bigger thumb to the required size
			loadedImage = CacheUtils.getRescaledInstance(imageCache, media.getFilehash(), size);
			if(loadedImage != null)
				imageCache.storeImage(media.getFilehash(), loadedImage);
		}

		if(loadedImage == null) {

			// we need to fetch the thumb directly from the source

			if(thumbImageCreator.canExtractThumb(media)){

				IMemoryImage maxThumb = thumbImageCreator.extractThumb(media, maxThumbSize);

				if(maxThumb != null){

					imageCache.storeImage(media.getFilehash(), maxThumb);

					if(size.equals(maxThumbSize)){
						loadedImage = maxThumb;
					}else{
						loadedImage = maxThumb.rescale(size.width, size.height);
						imageCache.storeImage(media.getFilehash(), loadedImage);
					}
				}
			}
		}

		return loadedImage;
	}

}