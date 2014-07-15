package vidada.server.rest.resource;

import vidada.server.rest.VidadaRestServer;
import vidada.server.services.ITagService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/tags")
public class TagsResource extends AbstractResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final ITagService tagService = VidadaRestServer.VIDADA_SERVER.getTagService();


	/**
	 * Returns all used tags
	 * @return
	 */
	@GET
	@Produces({MediaType.TEXT_PLAIN})
	public String get() {
		return "Tag Service";
	}


	/**
	 * Returns all used tags
	 * @return
	 */
	@GET
	@Path("used")
	@Produces({MediaType.APPLICATION_JSON})
	public String getUsed() {
		return serializeJson(tagService.getUsedTags());
	}
}
