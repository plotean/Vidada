package vidada.server.services;

import vidada.model.images.IThumbImageCreator;
import vidada.model.images.ThumbImageExtractor;
import vidada.model.images.cache.CacheUtils;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.model.media.store.local.LocalImageCacheManager;
import vidada.server.VidadaServer;
import vidada.server.settings.VidadaServerSettings;
import vidada.services.IThumbnailService;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public class ThumbnailService extends VidadaServerService implements IThumbnailService {

	transient private final Size maxThumbSize;
	transient private final IThumbImageCreator thumbImageCreator;
	transient private final LocalImageCacheManager localImageCacheManager;


	public ThumbnailService(VidadaServer server) {
		super(server);

		maxThumbSize = VidadaServerSettings.instance().getMaxThumbResolution();
		localImageCacheManager = new LocalImageCacheManager(VidadaServerSettings.instance().getAbsoluteCachePath());
		thumbImageCreator = new ThumbImageExtractor();
	}


	@Override
	public IMemoryImage getThumbImage(MediaItem media, Size size) {
		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		if(imagecache != null){
			try {
				thumb = fetchThumb(media, size, imagecache);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("LocalMediaStore: can not get image cache for " + media);
		}

		return thumb;
	}

	@Override
	public IMemoryImage renewThumbImage(MovieMediaItem media, Size size, float pos) {

		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		try {
			media.setPreferredThumbPosition(MovieMediaItem.INVALID_POSITION);
			media.setCurrentThumbPosition(MovieMediaItem.INVALID_POSITION);

			imagecache.removeImage(media.getFilehash());
			thumb = fetchThumb(media, size, imagecache);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return thumb;
	}

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
