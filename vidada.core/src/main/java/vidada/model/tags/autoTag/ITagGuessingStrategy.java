package vidada.model.tags.autoTag;

import java.util.Set;

import vidada.model.media.MediaItem;
import vidada.model.tags.Tag;

/**
 * Implements the AutoTag feature, so that for each media item
 * @author IsNull
 *
 */
public interface ITagGuessingStrategy {

	/**
	 * Guess all tags for this media item
	 * @param media
	 * @return
	 */
	public abstract Set<Tag> guessTags(MediaItem media);

}