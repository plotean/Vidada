package vidada.server.services;

import java.util.Collection;

import vidada.model.media.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.model.tags.relations.TagRelationDefinition;
import vidada.server.repositories.ITagRepository;
import vidada.server.repositories.RepositoryProvider;
import vidada.services.ITagService;

/**
 * Implements a {@link ITagService}
 * @author IsNull
 *
 */
public class TagService implements ITagService {

	//transient private final ITagService tagService;

	transient private final TagRelationDefinition relationDefinition = new TagRelationDefinition();
	transient private final ITagRepository repository = RepositoryProvider.Resolve(ITagRepository.class);

	/**
	 * Creates a new LocalTagService
	 * @param tagService
	 */
	public TagService(){
		//loadTagCacheFromDB();
	}

	/*
	private void loadTagCacheFromDB(){
		List<Tag> allTags = repository.getAllTags();
		tagService.cacheTags(allTags);
	}*/

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getAllRelatedTags(Tag tag){
		return relationDefinition.getAllRelatedTags(tag);
	}

	/**{@inheritDoc}*/
	@Override
	public void removeTag(Tag tag) {
		repository.delete(tag);
	}

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getUsedTags(Collection<MediaLibrary> libraries) {
		// TODO Cache this somehow or things get slow.
		// Realistically the result of this call will get invalidated
		// every time a tag is added or removed from a media item.
		return repository.getAllUsedTags(libraries);
	}

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getUsedTags() {
		// TODO Cache somehow
		return repository.getAllTags();
	}

}
