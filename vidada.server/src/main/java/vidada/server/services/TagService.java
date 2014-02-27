package vidada.server.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

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

	transient private final Map<String, Tag> tagCache = new HashMap<String, Tag>();

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
	public Collection<Tag> getUsedTags() {
		return runUnitOfWork(new Callable<Collection<Tag>>() {
			@Override
			public Collection<Tag> call() throws Exception {
				return repository.getAllTags();
			}
		});
	}

	/**{@inheritDoc}*/
	@Override
	public Tag getTag(String tagName) {
		tagName = Tag.toTagString(tagName);
		Tag tag = tagCache.get(tagName);
		if(tag == null){
			tag = Tag.create(tagName);
			tagCache.put(tagName, tag);
		}
		return tag;
	}

}
