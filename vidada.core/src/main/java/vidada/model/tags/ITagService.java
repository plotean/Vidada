package vidada.model.tags;

import java.util.Collection;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.IEvent;
import archimedesJ.services.IService;

/**
 * Provides functionality to manage tags
 * @author IsNull
 *
 */
public interface ITagService extends IService
{
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
	 * Returns all tags
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();


	/**
	 * Persistence - Update the tag
	 * @param tag
	 */
	//public abstract void update(Tag tag);

	/**
	 * Notify the service that the tags have changed outside the environment
	 * This will cause a TagsChanged Event to be fired
	 */
	public abstract void notifyTagsChanged();

}