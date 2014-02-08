package vidada.model.media.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import vidada.model.media.MediaItem;
import vidada.model.media.store.local.LocalMediaStore;

public class MediaStoreService implements IMediaStoreService {

	transient private final LocalMediaStore localMediaStore = new LocalMediaStore();
	transient private final Map<String, IMediaStore> allStores = new HashMap<String, IMediaStore>();

	public MediaStoreService(){
		addStore(localMediaStore);
	}

	private void addStore(IMediaStore store){
		allStores.put(store.getNameId(), store);
	}

	@Override
	public LocalMediaStore getLocalMediaStore(){
		return localMediaStore;
	}

	@Override
	public Collection<IMediaStore> getStores(){
		return allStores.values();
	}

	@Override
	public IMediaStore getStore(MediaItem media) {
		return allStores.get(media.getOriginId());
	}
}
