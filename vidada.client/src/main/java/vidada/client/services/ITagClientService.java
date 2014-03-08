package vidada.client.services;

import java.util.Collection;

import vidada.model.tags.Tag;

public interface ITagClientService {

	/**
	 * Returns all currently used tags
	 * @return
	 */
	public abstract Collection<Tag> getUsedTags();
}
