package vidada.repositories;

import java.util.Collection;
import java.util.List;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;

public interface ITagRepository extends IRepository<Tag> {

	public abstract void store(Tag newTag);

	public abstract void store(Collection<Tag> newTags);

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