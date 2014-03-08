package vidada.client.local.impl;

import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.pagination.ListPage;
import vidada.services.IMediaService;

public class LocalMediaClientService implements IMediaClientService {

	private IMediaService mediaService;

	public LocalMediaClientService(IMediaService mediaService){
		this.mediaService = mediaService;
	}

	@Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize) {
		return mediaService.query(qry, pageIndex, maxPageSize);
	}

	@Override
	public int count() {
		return mediaService.count();
	}

	@Override
	public void update(MediaItem media) {
		mediaService.update(media);
	}
}
