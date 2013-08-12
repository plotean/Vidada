package vidada.data;

import java.util.HashSet;
import java.util.Set;

import vidada.model.tags.ITagService;
import vidada.model.tags.Tag;
import vidada.model.tags.TagService;

public class DefaultDataCreator {

	public static void createDefaultData(){

		System.out.println("Creating default data...");

		ITagService tagService = new TagService();

		Set<Tag> tags = new HashSet<Tag>();

		tags.add( tagService.createTag("Action") );
		tags.add( tagService.createTag("Comedy") );
		tags.add( tagService.createTag("Horror") );
		tags.add( tagService.createTag("1020p") );
		tags.add( tagService.createTag("720p") );

		tagService.addTags(tags);

	}

}
