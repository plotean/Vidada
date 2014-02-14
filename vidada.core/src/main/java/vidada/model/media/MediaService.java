package vidada.model.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
	public List<MediaItem> query(MediaQuery qry) {

		OrderProperty order = qry.getOrder();

		// We don't need results ordered since it will be 
		// scrambled by the Hash-set merging anyway:
		qry.setOrder(OrderProperty.NONE);
		Set<MediaItem> resultSet = new HashSet<MediaItem>();
		System.out.println("MediaService:: query " + mediaStoreService.getStores().size() + " stores!");
		for (IMediaStore store : mediaStoreService.getStores()) {
			resultSet.addAll(store.query(qry));
		}

		// Now we have to sort the resultSet in memory
		// since we destroyed the order by using a hash map
		// and having multiple query result sets merged
		TreeSet<MediaItem> orderedMedias = new TreeSet<MediaItem>(MediaComparator.build(order, qry.isReverseOrder()));
		orderedMedias.addAll(resultSet);

		// restore order to original
		qry.setOrder(order);

		return new ArrayList<MediaItem>(orderedMedias);
	}


	@Override
	public void update(MediaItem media) {
		IMediaStore store = mediaStoreService.getStore(media);
		store.update(media);
	}


}
