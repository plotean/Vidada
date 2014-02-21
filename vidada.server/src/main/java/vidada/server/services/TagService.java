package vidada.server.services;

import java.util.Collection;
import java.util.concurrent.Callable;

import vidada.model.media.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.model.tags.relations.TagRelationDefinition;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.ITagRepository;
import vidada.services.ITagService;

/**
 * Implements a {@link ITagService}
 * @author IsNull
 *
 */
public class TagService extends VidadaServerService implements ITagService {

	//transient private final ITagService tagService;

	transient private final TagRelationDefinition relationDefinition = new TagRelationDefinition();
	transient private final ITagRepository repository = getRepository(ITagRepository.class);

	public TagService(VidadaServer server) {
		super(server);
	}


	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getAllRelatedTags(Tag tag){
		return relationDefinition.getAllRelatedTags(tag);
	}

	/**{@inheritDoc}*/
	@Override
	public void removeTag(final Tag tag) {
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				repository.delete(tag);
			}
		});
	}

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getUsedTags(final Collection<MediaLibrary> libraries) {
		// TODO Cache this somehow or things get slow.
		// Realistically the result of this call will get invalidated
		// every time a tag is added or removed from a media item.
		return runUnitOfWork(new Callable<Collection<Tag>>() {
			@Override
			public Collection<Tag> call() throws Exception {
				return repository.getAllUsedTags(libraries);
			}
		});
	}

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getUsedTags() {
		return runUnitOfWork(new Callable<Collection<Tag>>() {
			@Override
			public Collection<Tag> call() throws Exception {
				return repository.getAllTags();
			}
		});
	}

}
