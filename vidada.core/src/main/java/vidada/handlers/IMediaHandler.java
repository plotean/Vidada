package vidada.handlers;

import vidada.model.media.MediaItem;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Generic Handler to construct a chain of responsibility
 * @author IsNull
 *
 * @param <T>
 */
public interface IMediaHandler {

    /**
     * Returns the name of this media handler
     * @return
     */
    String getName();


	/**
	 * Try to handle the given media.
	 * If this handler was able to deal with the request it must return true.
	 * 
	 * @param media The media
	 * @param mediaResource The raw media resource location
	 * @return Returns true if handled successfully, false otherwise.
	 */
	boolean handle(MediaItem media, ResourceLocation mediaResource);
}
