package vidada.server.services;

import java.util.Collection;
import java.util.Set;

import vidada.model.tags.Tag;
import vidada.model.tags.relations.TagRelationDefinition;

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
	public Set<Tag> getAllRelatedTags(Tag tag);

	/**
	 * Returns all tags which are used by medias.
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();


	/**
	 * Removes the given tag from all medias
	 * @param tag
	 */
	public abstract void removeTag(Tag tag);


	// NON SERVICE METHODS :

	public abstract void mergeRelation(TagRelationDefinition relationDef);

	/**
	 * Gets the tag for the given string-name
	 * @return
	 */
	public abstract Tag getTag(String tagName);
}