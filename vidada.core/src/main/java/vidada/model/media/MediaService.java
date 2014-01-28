package vidada.model.media;

import java.util.HashSet;
import java.util.Set;

import vidada.model.ServiceProvider;
import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.IMediaStoreService;
import vidada.model.media.store.local.LocalMediaStore;

/**
 * Central service which manages all media items.
 * The media service manages all media-stores which then manage the details of the medias.
 * 
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class MediaService implements IMediaService {

	private IMediaStoreService mediaStoreService = ServiceProvider.Resolve(IMediaStoreService.class);

	// Events

	public MediaService(){

	}


	@Override
	public LocalMediaStore getLocalMediaStore(){
		return mediaStoreService.getLocalMediaStore();
	}


	@Override
	public Set<MediaItem> query(MediaQuery qry) {

		HashSet<MediaItem> resultSet = new HashSet<MediaItem>(2000);

		System.out.println("MediaService:: query " + mediaStoreService.getStores().size() + " stores!");
		for (IMediaStore store : mediaStoreService.getStores()) {
			resultSet.addAll(store.query(qry));
		}

		return resultSet;
	}


	@Override
	public void update(MediaItem media) {
		IMediaStore store = mediaStoreService.getStore(media);
		store.update(media);
	}


}
