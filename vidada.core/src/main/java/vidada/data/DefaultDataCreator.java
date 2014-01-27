package vidada.data;

import vidada.model.ServiceProvider;
import vidada.model.media.store.IMediaStoreService;
import vidada.model.media.store.local.LocalMediaStore;
import vidada.model.tags.ILocalTagService;


public class DefaultDataCreator {

	public static void createDefaultData(){

		/**/
		System.out.println("Creating default data...");

		IMediaStoreService storeService = ServiceProvider.Resolve(IMediaStoreService.class);
		LocalMediaStore localMediaStore = storeService.getLocalMediaStore();

		ILocalTagService tagService = localMediaStore.getTagManager(); 

		tagService.getTag("Action");
		tagService.getTag("Comedy");
		tagService.getTag("Horror");
		tagService.getTag("1020p");
		tagService.getTag("720p");
	}

}
