package vidada.model.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import vidada.model.ServiceProvider;

/**
 * Represents a Tag lookup cache using a map for fast indexing
 * 
 * 
 * 
 * @author IsNull
 *
 */
public class TagLookUpCache {

	private final ITagService tagService;
	private final Set<Tag> knownTags = new HashSet<Tag>();
	private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>();

	public TagLookUpCache(){
		this(ServiceProvider.Resolve(ITagService.class));
	}

	public TagLookUpCache(ITagService tagService){
		this.tagService = tagService;
		init();
	}

	/**
	 * Init the cache, cleaning up old data
	 */
	public void init() {

		clear();

		for (Tag tag : tagService.getAllTags()) {
			registerTag(tag.getName(), tag);
		}
	}

	/**
	 * Clear the cache
	 */
	public void clear(){
		knownTags.clear();
		tagNameCache.clear();
	}

	/**
	 * Register the keyword with the given tag
	 * @param keyword
	 * @param tag
	 */
	private void registerTag(String keyword,Tag tag){

		if(tag == null)
			throw new IllegalArgumentException("tag");

		tagNameCache.put(keyword.toLowerCase(), tag);
		knownTags.add(tag);
	}

	/**
	 * Creates a new Tag, and stores it in the lookup cache
	 * @param tagName
	 * @return
	 */
	public Tag createTag(String tagName){
		Tag tag = tagService.createTag(tagName);
		registerTag(tag.getName(), tag);
		return tag;
	}


	/**
	 * Finds a Tag by its name (case insensitive)
	 * @param tagName
	 * @return Returns the found Tag, or null if none could be found
	 */
	public Tag findTagByName(String tagName){
		return findTagByName(tagName, false);
	}

	/**
	 * Finds a Tag by the given name (case insensitive)
	 * Searches additionally in the Tags keywords for a match
	 * @param tagName
	 * @param searchInKeywords
	 * @return
	 */
	public Tag findTagByName(String tagName, boolean searchInKeywords){
		String tagNameKey = tagName.toLowerCase();

		if(!tagNameCache.containsKey(tagNameKey))
		{
			// Tag was not found. Probably we find it within a keyword of the tag
			Tag tag = findTagByKeyword(tagNameKey);

			if(tag != null)
			{
				registerTag(tagNameKey, tag);
			}
			tagNameCache.put(tagNameKey, tag);
		}
		return tagNameCache.get(tagNameKey);
	}


	private Tag findTagByKeyword(String keyword){
		for (Tag tag : knownTags) {
			for (TagKeyoword tagKeyoword : tag.getKeyWords()) {
				if(tagKeyoword.isMatch(keyword))
					return tag;
			}
		}
		return null;
	}



}
