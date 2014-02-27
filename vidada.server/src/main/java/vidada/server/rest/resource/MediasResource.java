package vidada.server.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.OrderProperty;
import vidada.model.pagination.ListPage;
import vidada.model.tags.Tag;
import vidada.server.rest.VidadaRestServer;
import vidada.services.IMediaService;
import vidada.services.ITagService;

@Path("/medias")
public class MediasResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	private final ITagService tagService = VidadaRestServer.VIDADA_SERVER.getTagService();


	/**
	 * Returns all medias
	 * @return
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public ListPage<MediaItem> getMedias(
			@QueryParam("query") String queryStr,
			@QueryParam("tags") List<String> tags,
			@QueryParam("type") vidada.model.media.MediaType type,
			@QueryParam("orderby") OrderProperty order) {

		MediaQuery query = new MediaQuery();
		query.setKeywords(queryStr);

		if(tags != null && !tags.isEmpty()){
			for (String tagStr : tags) {
				Tag tag = tagService.getTag(tagStr);
				query.getRequiredTags().add(tag);
			}
		}

		type = (type != null) ? type : vidada.model.media.MediaType.ANY;
		order = (order != null) ? order : OrderProperty.FILENAME;
		query.setSelectedtype(type);
		query.setOrder(order);


		return mediaService.query(query,0, 5); 
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
