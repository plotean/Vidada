package vidada.model.tags;


/**
 * Service which manages all Tags
 * @author IsNull
 *
 */
public class TagServiceOld { // implements ITagService 

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


	@Override
	public void addTag(Tag newTag){
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		db.store( newTag );
		db.commit();
		tagAddedEvent.fireEvent(this, EventArgsG.build(newTag));
		notifyTagsChanged();
	}

	@Override
	public void addTags(Set<Tag> newTags){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();

		for (Tag tag : newTags) {
			db.store( tag );
		}

		for (Tag tag : newTags) {
			tagAddedEvent.fireEvent(this, EventArgsG.build(tag));
		}

		db.commit();

		notifyTagsChanged();
	}

	@Override
	public void removeTag(Tag tag){

		ObjectContainer db = SessionManagerDB4O.getObjectContainer();


		{
			List<MediaItem> medias = QueryBuilderDB4O.buildMediadataCriteria(tag).execute();

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

	@Override
	public synchronized List<Tag> getAllTags(){

		List<Tag> tags = null;
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		Query query = db.query();
		query.constrain(Tag.class);
		tags = query.execute();
		tags = Lists.toList(tags);

		Collections.sort(tags);

		return tags;
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
				Tag newTag = createTag(tagName);
				if(newTag != null)
					parsedTags.add( newTag );
			}
		}

		return parsedTags;
	}

	@Override
	public Tag createTag(String tagName){
		Tag newTag = new Tag(tagName);
		newTag.addKeyword(tagName.toLowerCase());
		return newTag;
	}


	@Override
	public ITagGuessingStrategy createTagGuesser() {
		return new KeywordBasedTagGuesser(this.getAllTags());
	}



	@Override
	public synchronized void update(Tag tag) {
		ObjectContainer db =  SessionManagerDB4O.getObjectContainer();
		db.store(tag);
		notifyTagsChanged();
	}


	@Override
	public void notifyTagsChanged() {
		tagsChanged.fireEvent(this, EventArgs.Empty);
	}
	 */
}
