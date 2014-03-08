package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import vidada.server.rest.VidadaRestServer;
import vidada.services.ITagService;

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
	 * test
	 * @return
	 */
	@GET
	@Path("test")
	@Produces({MediaType.APPLICATION_JSON})
	public String getTest() {
		return "{test : bum }";
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
