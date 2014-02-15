package vidada.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;

public interface ITagRepository {

	public abstract void store(Tag newTag);

	public abstract void store(Set<Tag> newTags);

	public abstract void delete(Tag tag);

	/**
	 * Fetches all Tags which are used in the provided media libraries
	 * @return
	 */
	public abstract Collection<Tag> getAllUsedTags(
			Collection<MediaLibrary> libraries);

	/**
	 * Fetches all tags
	 * @return
	 */
	public abstract List<Tag> getAllTags();

	public abstract void update(Tag tag);

}