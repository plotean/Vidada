package vidada.services;


import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.services.IService;
import vidada.handlers.IMediaHandler;
import vidada.model.media.MediaItem;

import java.util.List;

/**
 * This service is responsible to present (open/play) a {@link MediaItem} to the user.
 * 
 * @author IsNull
 *
 */
public interface IMediaPresenterService extends IService {

	/**
	 * Show the given Media to the user.
	 * 
	 * This will open the media either inside Vidada (if supported and desired) 
	 * or with an external program.
	 * 
	 * @param media
	 * @return
	 */
	boolean showMedia(MediaItem media, ResourceLocation mediaResource);


	/**
	 * Adds the given handler as specialisation to the current successor.
	 * Note that the order in which you register handlers is important.
	 * 
	 * Usually, you will register generic handlers first followed by specialized ones.
	 * 
	 * @param handler
	 */
	void chainMediaHandler(IMediaHandler handler);


    /**
     * Returns all registered media handlers
     * @return
     */
    List<IMediaHandler> getAllMediaHandlers();

}
