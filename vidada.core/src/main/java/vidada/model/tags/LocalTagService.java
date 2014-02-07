package vidada.model.tags;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.autoTag.ITagGuessingStrategy;
import vidada.model.tags.autoTag.KeywordBasedTagGuesser;
import vidada.model.tags.relations.TagRelationDefinition;


public class LocalTagService implements ILocalTagService {

	transient private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>(5000);
	transient private final TagRelationDefinition relationDefinition = new TagRelationDefinition();
	transient private final TagRepository repository = new TagRepository();

	public LocalTagService(){
		loadTagCacheFromDB();
	}

	private void loadTagCacheFromDB(){
		List<Tag> allTags = repository.getAllTags();
		for (Tag tag : allTags) {
			tagNameCache.put(tag.getName(), tag);
		}
	}

	@Override
	public Collection<Tag> getAllRelatedTags(Tag tag){
		return relationDefinition.getAllRelatedTags(tag);
	}

	@Override
	public Tag getTag(String tagName){
		tagName = tagName.toLowerCase();
		Tag tag = tagNameCache.get(tagName);
		if(tag == null){
			if(isValidTag(tagName)){
				tag = new Tag(tagName);
				tagNameCache.put(tagName, tag);
				repository.store(tag);
			}
		}
		return tag;
	}

	private boolean isValidTag(String tagName){
		if(tagName == null || tagName.isEmpty())
			return false;
		return true;
	}

	@Override
	public Set<Tag> createTags(String tagString) {
		Set<Tag> parsedTags = new HashSet<Tag>();
		String[] tags = tagString.split("[,|\\|]");

		String tagName;
		for (String t : tags) {
			tagName = t.trim();
			if(!tagName.isEmpty())
			{
				Tag newTag = getTag(tagName);
				if(newTag != null)
					parsedTags.add( newTag );
			}
		}

		return parsedTags;
	}

	@Override
	public void removeTag(Tag tag) {
		repository.delete(tag);
	}

	@Override
	public Collection<Tag> getUsedTags() {
		return tagNameCache.values();
	}

	@Override
	public Collection<Tag> getUsedTags(Collection<MediaLibrary> libraries) {
		// TODO Cache this somehow or things get slow.
		// Realistically the result of this call will get invalidated
		// every time a tag is added or removed from a media item.
		return repository.getAllUsedTags(libraries);
	}

	@Override
	public ITagGuessingStrategy createTagGuesser() {
		return new KeywordBasedTagGuesser(this.getUsedTags());
	}

}
