package vidada.viewmodel;

import java.util.List;

import vidada.model.tags.Tag;

public interface ITagStatesVM extends ITagStatesVMProvider {

	/**
	 * Removes all tags from the model
	 */
	public abstract void clear();

	/**
	 * Adds all tags to the model
	 * @param newtags
	 */
	public abstract void addAll(List<Tag> newtags);

	/**
	 * Add the given tag to this model
	 * @param newTag
	 */
	public abstract void add(Tag newTag);

	/**
	 * Remove the given tag from the model
	 * @param tag
	 */
	public abstract void remove(Tag tag);

}