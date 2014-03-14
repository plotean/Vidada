package vidada.server.services;

import vidada.IVidadaServer;
import vidada.model.media.MediaItem;
import vidada.model.media.MovieMediaItem;
import archimedesJ.geometry.Size;
import archimedesJ.images.IMemoryImage;

/**
 * Manages all of a {@link IVidadaServer}
 * @author IsNull
 *
 */
public interface IThumbnailService {

	/**
	 * Gets the current thumb image of the given media in the requested size
	 * @param media
	 * @param size
	 * @return
	 */
	public IMemoryImage getThumbImage(MediaItem media, Size size);

	/**
	 * For movie medias, creates a new thumb at the specific position
	 * @param media
	 * @param size
	 * @param pos
	 * @return
	 */
	public void renewThumbImage(MovieMediaItem media, float pos);

}
