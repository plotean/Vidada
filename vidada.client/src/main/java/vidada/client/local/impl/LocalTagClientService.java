package vidada.client.local.impl;

import java.util.Collection;

import vidada.client.services.ITagClientService;
import vidada.model.tags.Tag;
import vidada.services.ITagService;

public class LocalTagClientService implements ITagClientService {

	private ITagService tagService;

	public LocalTagClientService(ITagService tagService){
		this.tagService = tagService;
	}

	@Override
	public Collection<Tag> getUsedTags() {
		return tagService.getUsedTags();
	}

}
