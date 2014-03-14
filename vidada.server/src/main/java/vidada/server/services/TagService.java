package vidada.server.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.model.tags.relations.TagRelationDefinition;
import vidada.server.VidadaServer;
import vidada.server.dal.repositories.ITagRepository;
import archimedesJ.util.Lists;

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

		addKnownTagsToCache();
	}


	/**{@inheritDoc}*/
	@Override
	public Set<Tag> getAllRelatedTags(Tag tag){
		return relationDefinition.getAllRelatedTags(tag);
	}

	/**{@inheritDoc}*/
	@Override
	public Set<Tag> getAllRelatedTags(Collection<Tag> tags) {
		Set<Tag> allrelated = new HashSet<Tag>();

		System.out.println("Expanding the given Tags: " + tags);

		if(!tags.isEmpty()){
			relationDefinition.print();

			for (Tag tag : tags) {
				allrelated.addAll(getAllRelatedTags(tag));
			}

			System.out.println("Expanded to: " + tags);
		}

		return allrelated;
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
			final Tag newTag = tag = TagFactory.instance().createTag(tagName);
			ensureTagIsInDB(Lists.asList(newTag));
			tagCache.put(tagName, tag);
		}
		return tag;
	}


	@Override
	public void mergeRelation(TagRelationDefinition relationDef) {
		relationDefinition.merge(relationDef);
		ensureTagIsInDB(relationDef.getAllTags());
		// ensure that all known tags are in the Database
	}



	private void addKnownTagsToCache(){
		Collection<Tag> alltags = runUnitOfWork(new Callable<Collection<Tag>>() {
			@Override
			public Collection<Tag> call() throws Exception {
				return repository.getAllTags();
			}
		});
		for (Tag tag : alltags) {
			tagCache.put(tag.getName(), tag);
		}
	}

	private void ensureTagIsInDB(final Collection<Tag> tags){
		runUnitOfWork(new Runnable() {
			@Override
			public void run() {
				for (Tag tag : tags) {
					Tag found = repository.queryById(tag.getName());
					if(found == null){
						repository.store(tag);
					}
				}
			}
		});
	}

}
