package vidada.model.tags;

import java.util.Collection;
import java.util.List;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.autoTag.ITagGuessingStrategy;
import vidada.model.tags.autoTag.KeywordBasedTagGuesser;
import vidada.model.tags.relations.TagRelationDefinition;
import vidada.repositories.ITagRepository;
import vidada.repositories.db4o.TagRepositoryDb4o;

/**
 * Implements a {@link ILocalTagService} 
 * @author IsNull
 *
 */
public class LocalTagService implements ILocalTagService {

	transient private final ITagService tagService;

	transient private final TagRelationDefinition relationDefinition = new TagRelationDefinition();
	transient private final ITagRepository repository = new TagRepositoryDb4o();

	/**
	 * Creates a new LocalTagService
	 * @param tagService
	 */
	public LocalTagService(ITagService tagService){
		this.tagService = tagService;
		loadTagCacheFromDB();
	}

	private void loadTagCacheFromDB(){
		List<Tag> allTags = repository.getAllTags();
		tagService.cacheTags(allTags);
	}

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
	public ITagGuessingStrategy createTagGuesser() {
		return new KeywordBasedTagGuesser(this.getUsedTags());
	}

	/**{@inheritDoc}*/
	@Override
	public Collection<Tag> getUsedTags() {
		// TODO Cache somehow
		return repository.getAllTags();
	}

}
