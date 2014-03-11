package vidada.server;

import vidada.IVidadaServer;
import vidada.server.services.ITagService;



public class DefaultDataCreator {

	public static void createDefaultData(IVidadaServer server){
		/* */
		System.out.println("Creating default data...");


		ITagService tagService = server.getTagService();
		/*		
		tagService.getTag("Action");
		tagService.getTag("Comedy");
		tagService.getTag("Horror");
		tagService.getTag("Thriller");

		tagService.getTag("1020p");
		tagService.getTag("720p");
		tagService.getTag("480p");
		tagService.getTag("360p");
		 */

	}

}
