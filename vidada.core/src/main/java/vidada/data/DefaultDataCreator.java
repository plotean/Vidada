package vidada.data;

import vidada.model.ServiceProvider;
import vidada.model.tags.ITagService;



public class DefaultDataCreator {

	public static void createDefaultData(){
		/* */
		System.out.println("Creating default data...");

		ITagService tagService = ServiceProvider.Resolve(ITagService.class);

		tagService.getTag("Action");
		tagService.getTag("Comedy");
		tagService.getTag("Horror");
		tagService.getTag("Thriller");

		tagService.getTag("1020p");
		tagService.getTag("720p");
		tagService.getTag("480p");
		tagService.getTag("360p");

	}

}
