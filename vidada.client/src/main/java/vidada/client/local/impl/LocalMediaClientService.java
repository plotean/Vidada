package vidada.client.local.impl;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;
import archimedes.core.io.locations.ResourceLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.client.services.IMediaClientService;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.source.MediaSource;
import vidada.server.services.IMediaService;

public class LocalMediaClientService implements IMediaClientService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(LocalMediaClientService.class.getName());
	private IMediaService mediaService;

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

    private final EventHandlerEx<EventArgs> mediasChangedEvent = new EventHandlerEx<EventArgs>();
    @Override
    public IEvent<EventArgs> getMediasChanged() { return mediasChangedEvent; }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new LocalMediaClientService
     * @param mediaService
     */
	public LocalMediaClientService(IMediaService mediaService){
		this.mediaService = mediaService;

        this.mediaService.getMediasChangedEvent().add(new EventListenerEx<EventArgs>() {
            @Override
            public void eventOccured(Object o, EventArgs args) {
                mediasChangedEvent.fireEvent(this, args);
            }
        });
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
	public ListPage<MediaItem> query(MediaQuery qry, int pageIndex, int maxPageSize) {
		return mediaService.query(qry, pageIndex, maxPageSize);
	}

	@Override
	public int count() {
		return mediaService.count();
	}

	@Override
	public void update(MediaItem media) {
		mediaService.update(media);
	}

	@Override
	public ResourceLocation openResource(MediaItem media) {
		ResourceLocation resource = null;
		MediaSource source = media.getSource();
		if(source != null){
			resource = source.getResourceLocation();
		}else{
            logger.error("Media " + media + " has no available source. Total sources: " + media.getSources());
		}
		return resource;
	}
}
