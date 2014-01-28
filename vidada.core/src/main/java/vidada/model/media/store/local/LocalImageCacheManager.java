package vidada.model.media.store.local;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import vidada.model.ServiceProvider;
import vidada.model.images.cache.IImageCache;
import vidada.model.images.cache.ImageCacheProxyBase;
import vidada.model.images.cache.MemoryImageCache;
import vidada.model.images.cache.crypto.ImageCacheFactory;
import vidada.model.media.MediaItem;
import vidada.model.media.source.IMediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.security.ICredentialManager;
import vidada.model.settings.GlobalSettings;
import archimedesJ.io.locations.DirectoryLocation;

public class LocalImageCacheManager {

	transient private final ICredentialManager credentialManager =  ServiceProvider.Resolve(ICredentialManager.class);

	transient private final IImageCache localImageCache;
	transient private final ImageCacheFactory cacheFactory = new ImageCacheFactory();
	transient private final Map<MediaLibrary, IImageCache> combinedCachesMap = new HashMap<MediaLibrary, IImageCache>();

	public LocalImageCacheManager(){
		localImageCache = openLocalCache();
	}


	/**
	 * Gets the image cache for the given media
	 * @param media
	 * @return
	 */
	public IImageCache getImageCache(MediaItem media){
		IImageCache imageCache;

		IMediaSource source = media.getSource();
		if(source != null)
		{
			MediaLibrary library = ((MediaSourceLocal)source).getParentLibrary();

			imageCache = combinedCachesMap.get(library);
			if(imageCache == null){
				IImageCache libraryCache = library.getLibraryCache();
				imageCache = cacheFactory.leveledCache(localImageCache, libraryCache);
				combinedCachesMap.put(library, imageCache);
			}
		} else {
			imageCache = localImageCache;
		}

		return imageCache;
	}


	/**
	 * Opens the local image cache
	 * Should this fail, it will return a dummy cache
	 * @return
	 */
	private IImageCache openLocalCache() {
		DirectoryLocation localCacheLocation = null;
		try {
			localCacheLocation = DirectoryLocation.Factory.create(
					GlobalSettings.getInstance().getAbsoluteCachePath().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IImageCache cache = cacheFactory.openEncryptedCache(localCacheLocation, credentialManager);
		if(cache != null){
			return cache;
		}else{
			System.err.println("Injecting a MemoryCache to replace LocalFile Cache");
			return new MemoryImageCache(new ImageCacheProxyBase(null));
		}
	}


}
