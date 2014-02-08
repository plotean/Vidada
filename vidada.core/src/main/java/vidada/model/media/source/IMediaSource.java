package vidada.model.media.source;

import archimedesJ.io.locations.ResourceLocation;

/**
 * Represents a media source. This can be a local file, a web resource, smb shared resource
 * or anything other which can deliver a stream
 * 
 * @author IsNull
 *
 */
public interface IMediaSource {

	/**
	 * Gets the media resource location 
	 * @return
	 */
	public abstract ResourceLocation getResourceLocation();

	/**
	 * Is the resource available?
	 * @return
	 */
	public boolean isAvailable();
}