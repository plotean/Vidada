package vidada.controller.tags;

import java.util.List;

import vidada.model.tags.Tag;
import vidada.model.tags.TagFilterState;

public interface ITagsView {

	/**
	 * Gets all tags in this tag view which have the given state
	 * @param checked
	 * @return
	 */
	List<Tag> getTagsWithState(TagFilterState filterState);

}
