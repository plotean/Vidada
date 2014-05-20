package vidada.handlers;

import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.util.Lists;
import vidada.model.media.MediaItem;
import vidada.services.IMediaPresenterService;

import java.util.ArrayList;
import java.util.List;


public class MediaPresenterService implements IMediaPresenterService {

	private final List<IMediaHandler> handlerChain = new ArrayList<IMediaHandler>();

	public MediaPresenterService(){ 
		configKnownHandlers();
	}

	@Override
	public synchronized boolean showMedia(MediaItem media, ResourceLocation mediaResource) {
		IMediaHandler handler;
		for (int i = 0; i < handlerChain.size(); i++) {
			handler = handlerChain.get( handlerChain.size() - (1 + i));
			if(handler.handle(media, mediaResource)) {
                return true;
            }
		}
		return false;
	}

	@Override
	public synchronized void chainMediaHandler(IMediaHandler mediaHandler) {
		handlerChain.add(mediaHandler);
	}

    @Override
    public List<IMediaHandler> getAllMediaHandlers() {
        return Lists.newList(handlerChain);
    }


    private void configKnownHandlers() {
		chainMediaHandler(new SystemDefaultOpenHandler());
	}

}
