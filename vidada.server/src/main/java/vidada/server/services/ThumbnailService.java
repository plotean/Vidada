package vidada.server.services;

import vidada.model.images.cache.IImageCache;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.model.media.store.local.LocalImageCacheManager;
import vidada.model.media.store.local.MediaThumbFetcher;
import vidada.services.IThumbnailService;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public class ThumbnailService implements IThumbnailService {

	transient private final MediaThumbFetcher localThumbFetcher = new MediaThumbFetcher();
	transient private final LocalImageCacheManager localImageCacheManager = new LocalImageCacheManager();


	@Override
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

	@Override
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
