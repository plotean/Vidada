package vidada.client.viewmodel;

import java.util.List;

import vidada.client.viewmodel.tags.TagViewModel;
import vidada.model.tags.Tag;
import vidada.model.tags.TagState;
import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.events.EventArgsG;
import archimedes.core.events.IEvent;

/**
 * This view model provides tag-states (only read is supported)
 * @author IsNull
 *
 */
public interface ITagStatesVMProvider {

	/**
	 * Raised when the tag model has changed
	 */
	public abstract IEvent<CollectionEventArg<Tag>> getTagsChangedEvent();

	// Raised when the state of a tag has been changed
	public abstract IEvent<EventArgsG<TagViewModel>> getTagStateChangedEvent();



	/**
	 * Return all tags with the given state
	 * @param state
	 * @return
	 */
	public abstract List<Tag> getTagsWithState(TagState state);

	/**
	 * Sets the given state to all tags
	 * @param state
	 */
	public abstract void setAllTagsState(TagState state);

	/**
	 * Sets the given state to the given tag
	 * @param tag
	 * @param state
	 */
	public abstract void setTagState(Tag tag, TagState state);

	/**
	 * Gets the state of the given tag
	 * @param tag
	 * @param state
	 */
	public abstract TagState getTagState(Tag tag);

	/**
	 * Get all tag view-models in this vm provider
	 * @return
	 */
	public abstract Iterable<TagViewModel> getTagViewModels();

	/**
	 * Get the view model for the given tag
	 * @param tag
	 * @return
	 */
	public abstract TagViewModel getViewModel(Tag tag);
}
