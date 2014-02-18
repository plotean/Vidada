package vidada.model.images;

import java.util.concurrent.Callable;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Represents a task which can retrieve a thumb for the given media item
 * @author IsNull
 *
 */
public class ImageLoaderTask implements Callable<IMemoryImage>
{
	transient private final IMediaStore mediaStore;
	transient private final MediaItem media;
	transient private final Size size;

	public ImageLoaderTask(IMediaStore mediaStore, MediaItem media, Size size){
		this.mediaStore = mediaStore;
		this.media = media;
		this.size = size;
	}

	@Override
	public IMemoryImage call() throws Exception {
		return mediaStore.getThumbImage(media, size);
	}
}