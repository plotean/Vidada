package vidada.model.connectivity;

import vidada.model.tags.Tag;

public class TagDTO {
	public TagDTO(Tag tag){
		Tag = tag.getName();
	}
	public String Tag;
}
