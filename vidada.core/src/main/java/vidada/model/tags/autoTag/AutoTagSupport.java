package vidada.model.tags.autoTag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;

public class AutoTagSupport {

	/**
	 * Updates the media item with tags according to its filename
	 * @param media
	 * @return Returns true if there where any tags added to the media
	 */
	public static boolean updateTags(ITagGuessingStrategy tagguesser, MediaItem media)
	{
		boolean tagsUpdated = false;

		Set<Tag> tags = filterTags(
				tagguesser.guessTags(media),
				media.getTags());

		if(!tags.isEmpty())
		{
			media.getTags().addAll(tags);
			tagsUpdated = true;
		}

		return tagsUpdated;
	}

	/**
	 * Filter the given tags, and returns all tags which are not in the ignore list
	 * @param tags The tags to filter
	 * @param tagsToIgnore Ignore list
	 * @return
	 */
	public static Set<Tag> filterTags(Set<Tag> tags, Collection<Tag> tagsToIgnore){

		Set<Tag> myTags = new HashSet<Tag>(tags.size());

		for (Tag tag : tags) {
			if(!tagsToIgnore.contains(tag))
			{
				myTags.add(tag);
			}
		}

		return myTags;

	}

}
