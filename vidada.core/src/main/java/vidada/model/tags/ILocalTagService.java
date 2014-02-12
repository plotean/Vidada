package vidada.model.tags;

import java.util.Collection;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.autoTag.ITagGuessingStrategy;

/**
 * Provides functionality to manage tags
 * @author IsNull
 *
 */
public interface ILocalTagService  {


	/**
	 * Gets all related tags for the provided one
	 * @param tag
	 * @return
	 */
	public Collection<Tag> getAllRelatedTags(Tag tag);


	/**
	 * Removes the given tag
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


	/**
	 * Creates a Tag guessing strategy for the current Tags
	 * @return
	 */
	public abstract ITagGuessingStrategy createTagGuesser();

}