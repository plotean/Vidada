package vidada.streaming;

import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.services.IService;
import archimedesJ.streaming.IResourceStreamServer;

public interface IStreamService extends IService {

	/**
	 * Creates a {@link IResourceStreamServer} which streams the requested resource.
	 * 
	 * @param mediaItem
	 */
	public abstract IResourceStreamServer streamMedia(ResourceLocation resource);
}
