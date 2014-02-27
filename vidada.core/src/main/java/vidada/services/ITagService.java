package vidada.services;

import java.util.Collection;

import vidada.model.tags.Tag;

/**
 * Manages all tags of a Vidada Server
 * @author IsNull
 *
 */
public interface ITagService  {


	/**
	 * Gets all related tags for the given one.
	 * @param tag
	 * @return
	 */
	public Collection<Tag> getAllRelatedTags(Tag tag);

	/**
	 * Removes the given tag from all medias
	 * @param tag
	 */
	public abstract void removeTag(Tag tag);

	/**
	 * Returns all tags which are used by medias.
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();


	// NON SERVICE METHODS :

	/**
	 * Gets the tag for the given string-name
	 * @return
	 */
	public abstract Tag getTag(String tagName);
}