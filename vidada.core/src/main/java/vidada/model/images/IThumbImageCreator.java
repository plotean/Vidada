package vidada.model.images;

import vidada.model.media.MediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

public interface IThumbImageCreator {

	public abstract boolean canExtractThumb(MediaItem media);

	/**
	 * 
	 * @param media
	 * @param size
	 * @return
	 */
	public abstract IMemoryImage extractThumb(MediaItem media, Size size);

}