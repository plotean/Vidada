package vidada.services;

import java.util.Collection;

import vidada.model.media.MediaLibrary;
import vidada.model.tags.Tag;

/**
 * Manages all tags of a Vidada Server
 * @author IsNull
 *
 */
public interface ITagService  {


	/**
	 * Gets all related tags for the provided one
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
	 * Returns all tags which are used by medias
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();


	/**
	 * Returns all tags which are used in the given libraries
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags(Collection<MediaLibrary> libraries);

}