package vidada.server.services;

import archimedes.core.data.pagination.ListPage;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.exceptions.NotSupportedException;
import archimedes.core.io.locations.ResourceLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import vidada.model.media.*;
import vidada.model.queries.*;
import vidada.model.tags.Tag;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.IMediaRepository;
import vidada.server.queries.MediaExpressionQuery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MediaService extends VidadaServerService implements IMediaService {

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    private static final Logger logger = LogManager.getLogger(MediaService.class.getName());

	transient private final IMediaRepository repository = getRepository(IMediaRepository.class);

    /***************************************************************************
     *                                                                         *
     * Events                                                                  *
     *                                                                         *
     **************************************************************************/

    private final EventHandlerEx<EventArgs> mediasChangedEvent = new EventHandlerEx<>();

    /**{@inheritDoc}*/
    @Override
    public IEvent<EventArgs> getMediasChangedEvent() { return mediasChangedEvent;  }

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new MediaService
     * @param server
     */
	public MediaService(VidadaServer server) {
		super(server);
	}

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
	public void store(final MediaItem media) {
		runUnitOfWork(() -> {
            repository.store(media);
        });
        onMediasChanged();
	}

	@Override
	public void store(final Collection<MediaItem> medias) {
		runUnitOfWork(() -> {
            repository.store(medias);
        });
        onMediasChanged();
	}

	@Override
	public void update(final MediaItem media) {
		runUnitOfWork(() -> {
            repository.update(media);
        });
	}

	@Override
	public void update(final Collection<MediaItem> medias) {
		runUnitOfWork(() -> {
            repository.update(medias);
        });
	}

	@Override
	public ListPage<MediaItem> query(final MediaQuery qry, final int pageIndex, final int maxPageSize) {
        return runUnitOfWork(() -> {

            // Add additional related tags to the users request
            // This is what vidada makes intelligent

            Expression<Tag> required = createTagExpression(qry.getRequiredTags(), false);
            Expression<Tag> blocked = createTagExpression(qry.getBlockedTags(), true);

            Expression<Tag> tagExpression = Expressions.and(required, blocked);

            logger.debug("Created tag expression: " + tagExpression.code());

            MediaExpressionQuery exprQuery = new MediaExpressionQuery(
                    tagExpression,
                    qry.getMediaType(),
                    qry.getKeywords(),
                    qry.getOrder(),
                    qry.isOnlyAvailable(),
                    qry.isReverseOrder());

            return repository.query(exprQuery, pageIndex, maxPageSize);
        });
    }


	@Override
	public List<MediaItem> getAllMedias(){
		return runUnitOfWork(() -> repository.getAllMedias());
	}

	@Override
	public void delete(final MediaItem media) {
		runUnitOfWork(() -> {
            repository.delete(media);
        });
        onMediasChanged();
	}

	@Override
	public void delete(final Collection<MediaItem> media) {
		runUnitOfWork(() -> {
            repository.delete(media);
        });
        onMediasChanged();
	}


	@Override
	public MediaItem findOrCreateMedia(ResourceLocation file, boolean persist) {
		return findAndCreateMedia(file, true, persist);
	}

    @Override
    public int count() {
        return runUnitOfWork(() -> repository.countAll()) ;
    }


    @Override
    public MediaItem queryByHash(final String hash) {
        return runUnitOfWork(() -> repository.queryByHash(hash)) ;
    }


    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Search for the given media data by the given absolute path
     */
    private MediaItem findMediaData(ResourceLocation file) {
        return findAndCreateMedia(file, false, false);
    }

	/**
	 * 
	 * @param resource
	 * @param canCreate
	 * @param persist
	 * @return
	 */
	private MediaItem findAndCreateMedia(final ResourceLocation resource, final boolean canCreate, final boolean persist){
		// We assume the given file is an absolute file path so we search for
		// a matching media library to substitute the library path

		return runUnitOfWork(() -> {
            MediaItem mediaData;

            IMediaLibraryService mediaLibraryService = getServer().getLibraryService();

            final MediaLibrary library = mediaLibraryService.findLibrary(resource);
            if(library != null){

                String hash = null;

                // first we search for the media

                mediaData = repository.queryByPath(resource, library);
                if(mediaData == null)
                {
                    hash = retrieveMediaHash(resource);
                    if(hash != null)
                        mediaData = repository.queryByHash(hash);
                }

                if(canCreate && mediaData == null){

                    // we could not find a matching media so we create a new one

                    mediaData = MediaItemFactory.instance().buildMedia(resource, library, hash);
                    if(persist && mediaData != null){
                        repository.store(mediaData);
                    }
                }
            }else
                throw new NotSupportedException("resource is not part of any media library");

            return mediaData;
        });

	}

	/**
	 * Gets the hash for the given file
	 * @param file
	 * @return
	 */
	private String retrieveMediaHash(ResourceLocation file){
		return MediaHashUtil.getDefaultMediaHashUtil().retrieveFileHash(file);
	}

    private Expression<Tag> createTagExpression(Collection<Tag> conjunction, boolean not){

        if(conjunction.isEmpty()) return null;

        ITagService tagService = getServer().getTagService();

        final VariableReferenceExpression<Tag> mediaTags = Expressions.varReference("m.tags");

        ListExpression<Tag> tagConjunction = ListExpression.createConjunction();

        for (Tag t : conjunction) {

            ListExpression<Tag> tagDisjunction =  ListExpression.createDisjunction();

            Set<Tag> relatedTags = tagService.getAllRelatedTags(t);
            for (LiteralValueExpression<String> relatedTag : Expressions.literalStrings(relatedTags)) {
                tagDisjunction.add(Expressions.memberOf(relatedTag, mediaTags));
            }
            if(!not) {
                tagConjunction.add(tagDisjunction);
            }else{
                tagConjunction.add(Expressions.not(tagDisjunction));
            }
        }

        return tagConjunction;
    }

    /**
     * Occurs when medias have been changed
     */
    private void onMediasChanged(){
        mediasChangedEvent.fireEvent(this, EventArgs.Empty);
    }
}
