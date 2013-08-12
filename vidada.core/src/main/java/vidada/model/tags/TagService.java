package vidada.model.tags;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vidada.data.SessionManager;
import vidada.model.media.MediaItem;
import vidada.model.media.QueryBuilder;
import vidada.model.tags.autoTag.ITagGuessingStrategy;
import vidada.model.tags.autoTag.KeywordBasedTagGuesser;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;
import archimedesJ.util.Lists;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

/**
 * Service which manages all Tags
 * @author IsNull
 *
 */
public class TagService implements ITagService {

	private transient final EventHandlerEx<EventArgsG<Tag>> tagAddedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgsG<Tag>> tagRemovedEvent = new EventHandlerEx<EventArgsG<Tag>>();
	private transient final EventHandlerEx<EventArgs> tagsChanged = new EventHandlerEx<EventArgs>();


	@Override
	public IEvent<EventArgs> getTagsChangedEvent() { return tagsChanged; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagAddedEvent() { return tagAddedEvent; }
	@Override
	public IEvent<EventArgsG<Tag>> getTagRemovedEvent() { return tagRemovedEvent; }


	/* (non-Javadoc)
	 * @see vidada.model.tags.ITagService#addTag(vidada.model.tags.Tag)
	 */
	@Override
	public void addTag(Tag newTag){
		ObjectContainer db = SessionManager.getObjectContainer();
		db.store( newTag );
		db.commit();
		tagAddedEvent.fireEvent(this, EventArgsG.build(newTag));
		notifyTagsChanged();
	}

	@Override
	public void addTags(Set<Tag> newTags){

		ObjectContainer db = SessionManager.getObjectContainer();

		for (Tag tag : newTags) {
			db.store( tag );
		}

		for (Tag tag : newTags) {
			tagAddedEvent.fireEvent(this, EventArgsG.build(tag));
		}
		db.commit();

		notifyTagsChanged();
	}

	/* (non-Javadoc)
	 * @see vidada.model.tags.ITagService#removeTag(vidada.model.tags.Tag)
	 */
	@Override
	public void removeTag(Tag tag){

		ObjectContainer db = SessionManager.getObjectContainer();


		{
			List<MediaItem> medias = QueryBuilder.buildMediadataCriteria(tag).execute();

			if(!medias.isEmpty())
			{
				// There are medias currently using the tag which will be deleted
				// Remove this tag from those medias first.
				for (MediaItem mediaItem : medias) {
					mediaItem.removeTag(tag);
					db.store(mediaItem);
				}
			}

			db.delete(tag);
			db.commit();
		}

		tagRemovedEvent.fireEvent(this, EventArgsG.build(tag));
		notifyTagsChanged();
	}

	/* (non-Javadoc)
	 * @see vidada.model.tags.ITagService#getAllTags()
	 */
	@Override
	public synchronized List<Tag> getAllTags(){

		List<Tag> tags = null;
		ObjectContainer db = SessionManager.getObjectContainer();
		Query query = db.query();
		query.constrain(Tag.class);
		tags = query.execute();
		tags = Lists.toList(tags);

		Collections.sort(tags);

		return tags;
	}


	/**
	 * Create one or more new tags of a delimited list:
	 * 
	 * action,fun,horror
	 * 
	 * Valid Delemiters are: 
	 * Comma 	,
	 * Pipe 	|
	 */
	@Override
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

	/**
	 * Creates a new Tag
	 * @param tagName
	 * @return
	 */
	@Override
	public Tag createTag(String tagName){
		Tag newTag = new Tag(tagName);
		newTag.addKeyword(tagName.toLowerCase());
		return newTag;
	}


	/**
	 * Creates a Tag Guessing strategy for the current Tags
	 */
	@Override
	public ITagGuessingStrategy createTagGuesser() {
		return new KeywordBasedTagGuesser(this.getAllTags());
	}


	//
	// TagKeywords
	//
	private final Map<String, TagKeyoword> keywordCache = new HashMap<String, TagKeyoword>();

	/**
	 * Returns the keyword for the given keywordname string
	 * 
	 * @param keywordName
	 * @return
	 */
	@Override
	public synchronized TagKeyoword getTagKeyoword(String keywordName){
		if(!keywordCache.containsKey(keywordName))
		{
			TagKeyoword keyword = new TagKeyoword(keywordName);

			ObjectContainer db =  SessionManager.getObjectContainer();

			db.store(keyword);
			keywordCache.put(keywordName, keyword);


		}

		return keywordCache.get(keywordName);
	}


	@Override
	public synchronized void update(Tag tag) {
		ObjectContainer db =  SessionManager.getObjectContainer();
		db.store(tag);
		notifyTagsChanged();
	}


	@Override
	public void notifyTagsChanged() {
		tagsChanged.fireEvent(this, EventArgs.Empty);
	}

}
