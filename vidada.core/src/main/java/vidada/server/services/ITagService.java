package vidada.server.services;

import vidada.model.tags.Tag;
import vidada.model.tags.relations.TagRelationDefinition;

import java.util.Collection;
import java.util.Set;

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
     * Only master tags are returned, no synonyms.
     *
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();

    /**
     * Returns all known tags, including synonyms (slave tags).
     * @return
     */
    public abstract Collection<Tag> getAllTags();

	/**
	 * Removes the given tag from all medias
	 * @param tag
	 */
	public abstract void removeTag(Tag tag);

    /**
     * Merges the given Tag Relations into this Tag-Service.
     * (This will cause this tag manager to learn this relations)
     *
     * @param relationDef
     */
	public abstract void mergeRelation(TagRelationDefinition relationDef);

	/**
	 * Gets the tag for the given string-name
	 * @return
	 */
	public abstract Tag getTag(String tagName);
}