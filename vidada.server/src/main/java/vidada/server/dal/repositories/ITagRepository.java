package vidada.server.dal.repositories;

import java.util.Collection;
import java.util.List;

import vidada.model.tags.Tag;

public interface ITagRepository extends IRepository {

	public abstract void store(Tag newTag);

	public abstract void store(Collection<Tag> newTags);

	public abstract void delete(Tag tag);

	/**
	 * Fetches all tags
	 * @return
	 */
	public abstract List<Tag> getAllTags();

	public abstract void update(Tag tag);

	public abstract Tag queryById(String tagName);

}