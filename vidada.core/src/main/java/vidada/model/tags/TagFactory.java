package vidada.model.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides helper methods to create Tag instances including a simple cache.
 * 
 * @author IsNull
 *
 */
public class TagFactory {

	// --- Singleton

	private TagFactory() {}

	private static TagFactory instance;

	public synchronized static TagFactory instance(){
		if(instance == null){
			instance = new TagFactory();
		}
		return instance;
	}

	// Singleton end ---

	transient private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>(5000);

	public Set<Tag> createTags(String tagString) {
		Set<Tag> parsedTags = new HashSet<Tag>();
		String[] tags = tagString.split("[,|\\|]");

		String tagName;
		for (String t : tags) {
			tagName = t.trim();
			if(!tagName.isEmpty())
			{
				Tag newTag = createTag(tagName);
				if(newTag != null)
					parsedTags.add( newTag );
			}
		}
		return parsedTags;
	}

	public Tag createTag(String tagName){
		tagName = toTagString(tagName);
		Tag tag = tagNameCache.get(tagName);
		if(tag == null){
			if(isValidTag(tagName)){
				tag = createTagInternal(tagName);
			}
		}
		return tag;
	}

    public String toTagString(String text){
        // TODO maybe replace special chars here as well?
        return text.trim().toLowerCase();
    }

	public boolean isValidTag(String tagName){
		if(tagName == null || tagName.isEmpty())
			return false;
		return true;
	}

	private Tag createTagInternal(String name){
		Tag tag = new Tag(name);
		tagNameCache.put(name, tag);
		return tag;
	}
}
