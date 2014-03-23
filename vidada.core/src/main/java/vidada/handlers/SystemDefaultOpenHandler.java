package vidada.handlers;

import vidada.model.media.MediaItem;
import vidada.model.system.ISystemService;
import vidada.services.ServiceProvider;
import archimedesJ.io.locations.ResourceLocation;

/**
 * Opens the media with the operating systems default registered
 * program for the given URI type.
 * 
 * This handler is usually the last being called due to its unpredictable outcome.
 * 
 * @author IsNull
 *
 */
public class SystemDefaultOpenHandler extends AbstractMediaHandler {

	private final ISystemService systemService = ServiceProvider.Resolve(ISystemService.class);

    public SystemDefaultOpenHandler(){super("System Default");}

	@Override
	public boolean handle(MediaItem media, ResourceLocation mediaResource) {
		systemService.open(mediaResource);
		return true;
	}
}
