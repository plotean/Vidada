package vidada.client.local.impl;

import vidada.client.services.IThumbnailClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import vidada.server.services.IThumbnailService;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public class LocalThumbnailClientService implements IThumbnailClientService {

	private IThumbnailService thumbnailService;

	public LocalThumbnailClientService(IThumbnailService thumbnailService){
		this.thumbnailService = thumbnailService;
	}

	@Override
	public IMemoryImage retrieveThumbnail(MediaItem media, Size size) {
		return thumbnailService.getThumbImage(media, size);
	}

	@Override
	public boolean renewThumbImage(MediaItem media, float pos) {
		thumbnailService.renewThumbImage((MovieMediaItem)media, pos);
		return true;
	}
}
