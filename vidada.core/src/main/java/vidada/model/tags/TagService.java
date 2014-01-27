package vidada.model.tags;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class TagService {

	transient private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>(5000);
	private TagRelationDefinition relationDefinition = new TagRelationDefinition();


	public Collection<Tag> getAllRelatedTags(Tag tag){
		return relationDefinition.getAllRelatedTags(tag);
	}

	public Tag getTag(String tagName){
		Tag tag = tagNameCache.get(tagName);
		if(tag == null){
			tag = new Tag(tagName);
		}
		return tag;
	}

}
