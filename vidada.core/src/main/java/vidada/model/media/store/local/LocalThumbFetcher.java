package vidada.model.media.store.local;

import vidada.model.images.IThumbImageCreator;
import vidada.model.images.ThumbImageExtractor;
import vidada.model.images.cache.CacheUtils;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.settings.GlobalSettings;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public class LocalThumbFetcher {

	transient private static final Size maxThumbSize = GlobalSettings.getInstance().getMaxThumbResolution();
	transient private final IThumbImageCreator thumbImageCreator = new ThumbImageExtractor();

	/**
	 *  Fetches a thumb from the given media in the specified size.
	 * 	The thumb will be retrieved from cache if possible.
	 * 
	 * @param media
	 * @param size
	 * @param imageCache
	 * @return
	 * @throws Exception
	 */
	public IMemoryImage fetchThumb(MediaItem media, Size size, IImageCache imageCache) throws Exception {

		IMemoryImage loadedImage = null;


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
