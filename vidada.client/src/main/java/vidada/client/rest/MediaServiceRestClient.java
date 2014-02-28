package vidada.client.rest;

import java.util.Collection;
import java.util.List;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import vidada.services.IMediaService;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.io.locations.ResourceLocation;

public class MediaServiceRestClient implements IMediaService {


	@Override
	public void update(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex,
			int maxPageSize) {
		throw new NotImplementedException();
	}

	@Override
	public int count() {
		throw new NotImplementedException();
	}


	// Not implemented in remote service

	@Override
	public void store(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public void store(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}


	@Override
	public void update(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}

	@Override
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		throw new NotImplementedException();
	}

	@Override
	public List<MediaItem> getAllMedias() {
		throw new NotImplementedException();
	}

	@Override
	public void delete(MediaItem media) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(Collection<MediaItem> media) {
		throw new NotImplementedException();
	}

	@Override
	public MediaItem queryByHash(String hash) {
		throw new NotImplementedException();
	}

}
