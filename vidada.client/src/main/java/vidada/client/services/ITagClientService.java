package vidada.client.services;

import java.util.Collection;
import java.util.Set;

import vidada.model.tags.Tag;

public interface ITagClientService {

	/**
	 * Returns all currently used tags
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();


	/**
	 * Checks if the given tag is valid
	 * @param tagName
	 * @return
	 */
	public abstract boolean isValidTag(String tagName);

	/**
	 * Get the corresponding tag object for the given name
	 * @param tag
	 * @return
	 */
	public abstract Tag getTag(String tag);


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
	 * Makes the given tags known, i.e. puts them in the cache
	 * @param tags
	 */
	public abstract void cacheTags(Collection<Tag> tags);
}
