package vidada.model.media.store.local;

import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Represents a local media store which manages all  medias available locally.
 * 
 * @author IsNull
 *
 */
public class LocalMediaStore {

	public static final String Name = "local.store"; 

	transient private final MediaThumbFetcher localThumbFetcher = new MediaThumbFetcher();
	transient private final LocalImageCacheManager localImageCacheManager = new LocalImageCacheManager();



	public IMemoryImage getThumbImage(MediaItem media, Size size) {
		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		if(imagecache != null){
			try {
				thumb = localThumbFetcher.fetchThumb(media, size, imagecache);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.err.println("LocalMediaStore: can not get image cache for " + media);
		}

		return thumb;
	}

	public IMemoryImage renewThumbImage(MovieMediaItem media, Size size, float pos) {

		IMemoryImage thumb = null;

		IImageCache imagecache = localImageCacheManager.getImageCache(media);

		try {
			media.setPreferredThumbPosition(MovieMediaItem.INVALID_POSITION);
			media.setCurrentThumbPosition(MovieMediaItem.INVALID_POSITION);

			imagecache.removeImage(media.getFilehash());
			thumb = localThumbFetcher.fetchThumb(media, size, imagecache);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return thumb;
	}



}
