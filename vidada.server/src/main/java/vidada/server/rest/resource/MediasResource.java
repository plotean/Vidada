package vidada.server.rest.resource;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;
import vidada.server.services.ITagService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/medias")
public class MediasResource extends AbstractResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	private final ITagService tagService = VidadaRestServer.VIDADA_SERVER.getTagService();

	// JAXBElement<MediaItem>
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(String mediaJson){
		MediaItem media = deserialize(mediaJson, MediaItem.class);
		mediaService.update(media);
		return Response.ok().build();
	}

	/**
	 * Returns all medias
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String getMedias(
			@QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize,
			@QueryParam("query") String queryStr,
			@QueryParam("tags") String alltags,
			@QueryParam("type") vidada.model.media.MediaType type,
			@QueryParam("orderby") OrderProperty order) {

		pageSize = pageSize == 0 ? 6 : pageSize;

		MediaQuery query = new MediaQuery();
		query.setKeywords(queryStr);

		String[] tags = parseMultiValueParam(alltags);
		if(tags != null && tags.length > 0){
			for (String tagStr : tags) {
				Tag tag = tagService.getTag(tagStr);
				query.getRequiredTags().add(tag);
			}
		}

		System.out.println("tags as objects:" + query.getRequiredTags());

		query.setSelectedtype((type != null) ? type : vidada.model.media.MediaType.ANY);
		query.setOrder((order != null) ? order : OrderProperty.FILENAME);

		System.out.println("delivering medias page: " + page + " pageSize: " + pageSize);

		return serializeJson(mediaService.query(query, page, pageSize)); 
	}


	/**
	 * Retuns the number of medias
	 * Use vidada/api/medias/count
	 * to get the total number of medias
	 * @return
	 */
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {		
		return String.valueOf(mediaService.count());
	}
}
