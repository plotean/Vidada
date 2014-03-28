package vidada.client.local.impl;

import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.source.MediaSource;
import vidada.model.pagination.ListPage;
import vidada.server.services.IMediaService;
import archimedes.core.io.locations.ResourceLocation;

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

	@Override
	public ResourceLocation openResource(MediaItem media) {
		ResourceLocation resource = null;
		MediaSource source = media.getSource();
		if(source != null){
			resource = source.getResourceLocation();
		}else{
			System.err.println("Media " + media + " has no available source. Total sources: " + media.getSources());
		}
		return resource;

	}
}
