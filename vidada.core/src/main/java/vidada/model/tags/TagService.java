package vidada.model.tags;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import vidada.model.ServiceProvider;
import vidada.model.media.store.IMediaStore;
import vidada.model.media.store.IMediaStoreService;


/**
 * Service which manages all Tags
 * @author IsNull
 *
 */
public class TagService implements ITagService {

	transient private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>(5000);

	/*
	private transient final EventHandlerEx<EventArgsG<Tag>> tagAddedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgsG<Tag>> tagRemovedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgs> tagsChanged = new EventHandlerEx<EventArgs>();

	@Override
	public IEvent<EventArgs> getTagsChangedEvent() { return tagsChanged; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagAddedEvent() { return tagAddedEvent; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagRemovedEvent() { return tagRemovedEvent; }
	 */


	public TagService(){

	}


	@Override
	public synchronized Collection<Tag> getUsedTags(){

		IMediaStoreService mediaStoreService = ServiceProvider.Resolve(IMediaStoreService.class);

		Set<Tag> allTags = new HashSet<Tag>();
		Collection<IMediaStore> stores = mediaStoreService.getStores();

		for (IMediaStore store : stores) {
			Collection<Tag> storeTags = store.getAllUsedTags();
			allTags.addAll(storeTags);
		}

		return allTags;
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
	public Tag getTag(String tagName){
		tagName = tagName.toLowerCase();
		Tag tag = tagNameCache.get(tagName);
		if(tag == null){
			if(isValidTag(tagName)){
				tag = new Tag(tagName);
				tagNameCache.put(tagName, tag);
			}
		}
		return tag;
	}

	@Override
	public boolean isValidTag(String tagName){
		if(tagName == null || tagName.isEmpty())
			return false;
		return true;
	}


	@Override
	public void cacheTags(Collection<Tag> tags) {
		for (Tag tag : tags) {
			tagNameCache.put(tag.getName(), tag);
		}
	}


}
