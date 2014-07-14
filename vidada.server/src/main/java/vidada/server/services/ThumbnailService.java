package vidada.server.services;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.images.GlobalImageCacheManager;
import vidada.model.images.IThumbImageExtractor;
import vidada.model.images.ThumbImageExtractor;
import vidada.model.images.cache.CacheUtils;
import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.server.VidadaServer;
import vidada.server.settings.VidadaServerSettings;
import archimedes.core.geometry.Size;
import archimedes.core.images.IMemoryImage;

/**
 * Basic implementation of a {@link IThumbnailService}
 *
 */
public class ThumbnailService extends VidadaServerService implements IThumbnailService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(ThumbnailService.class.getName());

	transient private final Size maxThumbSize;
	transient private final IThumbImageExtractor thumbImageCreator;
	transient private final GlobalImageCacheManager globalImageCacheManager;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new ThumbnailService
     * @param server
     */
	public ThumbnailService(VidadaServer server) {
		super(server);

		maxThumbSize = VidadaServerSettings.instance().getMaxThumbResolution();

        GlobalImageCacheManager globalCache;
        if(VidadaServerSettings.instance().getCurrentDBConfig().isUseLocalCache()){
            globalCache = new GlobalImageCacheManager(VidadaServerSettings.instance().getAbsoluteCachePath());
        }else{
            globalCache = new GlobalImageCacheManager(null); // TODO Clean up API
        }
		globalImageCacheManager = globalCache;
		thumbImageCreator = new ThumbImageExtractor();
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
	@Override
	public IMemoryImage getThumbImage(MediaItem media, Size size) {
		IMemoryImage thumb = null;

		IImageCache imageCache = globalImageCacheManager.getImageCache(media);

		if(imageCache != null){
			try {
				thumb = fetchThumb(media, size, imageCache);
			} catch (Exception e) {
                logger.error("Fetching thumbnail failed.", e);
			}
		}else{
            logger.error("Can not find image cache for " + media);
		}
		return thumb;
	}

    /** {@inheritDoc} */
	@Override
	public void renewThumbImage(MovieMediaItem media, float pos) {

		IImageCache imageCache = globalImageCacheManager.getImageCache(media);

		try {
			media.setPreferredThumbPosition(pos);
			media.setCurrentThumbPosition(MovieMediaItem.INVALID_POSITION);
			getServer().getMediaService().update(media);

			imageCache.removeImage(media.getFilehash());
		} catch (Exception e) {
            logger.error(e);
		}
	}



	/**
	 *  Fetches a thumb from the given media in the specified size.
	 * 	The thumb will be retrieved from a cached bigger version if possible.
	 * 
	 * @param media
	 * @param size
	 * @param imageCache
	 * @return
	 * @throws Exception
	 */
	private IMemoryImage fetchThumb(MediaItem media, Size size, IImageCache imageCache) throws Exception {

		IMemoryImage loadedImage;

		if(size == null) 
			size = maxThumbSize;

		loadedImage = imageCache.getImageById(media.getFilehash(), size);

		if(loadedImage == null){
			// Bad luck, the image does not exist in the cache

			// But we may already have cached a bigger version of the requested size.
			// If so, we rescale this bigger thumb to the required size.
			loadedImage = CacheUtils.getRescaledInstance(imageCache, media.getFilehash(), size);
			if(loadedImage != null)
				imageCache.storeImage(media.getFilehash(), loadedImage);
		}

		if(loadedImage == null) {

			// We need to fetch the thumb directly from the source.

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
