package vidada.model.media.info;

import vidada.model.media.MediaItem;
import vidada.model.media.extracted.IMediaPropertyStore;

/**
 * This service updates all media information which are extractable form the raw data.
 * I.e. resolution, bitrate / duration and such.
 * 
 * 
 * 
 * @author IsNull
 *
 */
public interface IMediaInfoUpdateService {

	/**
	 * Updates the media info from the media raw data. 
	 * Properties like...
	 * - resolution
	 * - duration
	 * - etc
	 * ... depending on the media type and information present in the raw data.
	 * @param media
	 * @return Returns true if there where any information changed in the media.
	 */
	boolean updateInfo(MediaItem media);

	/**
	 * Updates the media info from the given {@link IMediaPropertyStore}
	 * @param media The media to update
	 * @param propertyStore The {@link IMediaPropertyStore} to use for lookup and persisting
	 * @return
	 */
	boolean updateInfoFromCache(MediaItem media, IMediaPropertyStore propertyStore);

}