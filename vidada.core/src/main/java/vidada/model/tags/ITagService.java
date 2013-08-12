package vidada.model.tags;

import java.util.List;
import java.util.Set;

import vidada.model.tags.autoTag.ITagGuessingStrategy;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;

/**
 * Provides functionality to manage tags
 * @author IsNull
 *
 */
public interface ITagService {

	/**
	 * Raised when one or more Tags have been changed
	 * @return
	 */
	public abstract IEvent<EventArgs> getTagsChangedEvent();

	/**
	 * Raised when a Tag has been added
	 * @return
	 */
	public abstract IEvent<EventArgsG<Tag>> getTagAddedEvent();

	/**
	 * Raised when a Tag has been removed
	 * @return
	 */
	public abstract IEvent<EventArgsG<Tag>> getTagRemovedEvent();


	/**
	 * Create one or more new tags of a delimited list:
	 * 
	 * action,fun,horror
	 * 
	 * Valid Delemiters are: 
	 * Comma 	,
	 * Pipe 	|
	 *  
	 * @param tagString
	 * @return
	 */
	public abstract Set<Tag> createTags(String tagString);

	/**
	 * Creates a new Tag with the given name
	 * @param tagName
	 * @return
	 */
	public abstract Tag createTag(String tagName);

	/**
	 * Adds the given new tag
	 * @param newTag
	 */
	public abstract void addTag(Tag newTag);

	/**
	 * 
	 * @param newTags
	 */
	public abstract void addTags(Set<Tag> newTags);

	/**
	 * Removes the given tag
	 * @param tag
	 */
	public abstract void removeTag(Tag tag);

	/**
	 * Returns all tags
	 * @return
	 */
	public abstract List<Tag> getAllTags();


	/**
	 * Creates a Tag guessing strategy for the current Tags
	 * @return
	 */
	public abstract ITagGuessingStrategy createTagGuesser();


	/**
	 * Returns the keyword for the given keywordname string
	 * 
	 * @param keywordName
	 * @return
	 */
	public abstract TagKeyoword getTagKeyoword(String keywordName);

	/**
	 * Persistence - Update the tag
	 * @param tag
	 */
	public abstract void update(Tag tag);

	/**
	 * Notify the service that the tags have changed outside the environment
	 * This will cause a TagsChanged Event to be fired
	 */
	public abstract void notifyTagsChanged();

}