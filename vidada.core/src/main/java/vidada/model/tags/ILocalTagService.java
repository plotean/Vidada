package vidada.model.tags;

import java.util.Collection;
import java.util.Set;

import vidada.model.tags.autoTag.ITagGuessingStrategy;

/**
 * Provides functionality to manage tags
 * @author IsNull
 *
 */
public interface ILocalTagService  {

	/**
	 * Get the tag for the given tag-string
	 * @param tagName
	 * @return
	 */
	public Tag getTag(String tagName);


	/**
	 * Gets all related tags for the provided one
	 * @param tag
	 * @return
	 */
	public Collection<Tag> getAllRelatedTags(Tag tag);


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
	 * Removes the given tag
	 * @param tag
	 */
	public abstract void removeTag(Tag tag);

	/**
	 * Returns all tags
	 * @return
	 */
	public abstract Collection<Tag> getAllTags();

	/**
	 * Creates a Tag guessing strategy for the current Tags
	 * @return
	 */
	public abstract ITagGuessingStrategy createTagGuesser();

}