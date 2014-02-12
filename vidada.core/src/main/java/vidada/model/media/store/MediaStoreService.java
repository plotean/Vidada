package vidada.model.media.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.store.local.LocalMediaStore;
import vidada.model.tags.ITagService;

public class MediaStoreService implements IMediaStoreService {

	transient private LocalMediaStore localMediaStore = null;
	transient private final Map<String, IMediaStore> allStores = new HashMap<String, IMediaStore>();

	public MediaStoreService(){

	}

	private void addStore(IMediaStore store){
		allStores.put(store.getNameId(), store);
	}

	@Override
	public LocalMediaStore getLocalMediaStore(){
		if(localMediaStore == null){
			// Lazy load ITagService dependencies since this service depends on others
			ITagService tagService = ServiceProvider.Resolve(ITagService.class);
			localMediaStore = new LocalMediaStore(tagService);
			addStore(localMediaStore);
		}
		return localMediaStore;
	}

	@Override
	public Collection<IMediaStore> getStores(){
		getLocalMediaStore(); // ensures that the local store is present
		return allStores.values();
	}

	@Override
	public IMediaStore getStore(MediaItem media) {
		getLocalMediaStore(); // ensures that the local store is present
		return allStores.get(media.getOriginId());
	}
}
