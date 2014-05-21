package vidada.server.rest.resource;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.OrderProperty;
import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;
import vidada.server.rest.VidadaRestServer;
import vidada.server.services.IMediaService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

@Path("/medias")
public class MediasResource extends AbstractResource {

	// Allows to insert contextual objects into the class, 
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	//private final ITagService tagService = VidadaRestServer.VIDADA_SERVER.getTagService();

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
			@QueryParam("page") @DefaultValue("0") int page,
			@QueryParam("pageSize") @DefaultValue("6") int pageSize,
			@QueryParam("query") String queryStr,
			@QueryParam("tags") String requiredTags,
            @QueryParam("tagsNot") String blockedTags,
			@QueryParam("type") vidada.model.media.MediaType type,
			@QueryParam("orderBy") OrderProperty order,
            @QueryParam("reverse") @DefaultValue("0") boolean reverse) {

		MediaQuery query = new MediaQuery();
		query.setKeywords(queryStr);

        query.getRequiredTags().addAll(parseTags(requiredTags));
        query.getBlockedTags().addAll(parseTags(blockedTags));

		query.setSelectedtype((type != null) ? type : vidada.model.media.MediaType.ANY);
		query.setOrder((order != null) ? order : OrderProperty.FILENAME);
        query.setReverseOrder(reverse);

		System.out.println("Delivering medias page: " + page + " pageSize: " + pageSize);

		return serializeJson(mediaService.query(query, page, pageSize)); 
	}


	/**
	 * Returns the number of medias
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

    /**
     * Gets the detail of a single media
     * @param hash
     * @return
     */
    @GET
    @Path("{hash}")
    @Produces({ MediaType.APPLICATION_JSON })
    public MediaResource getMedia(@PathParam("hash") String hash) {
        return new MediaResource(hash);
    }


    /**
     * Parses the tags query param string into Tag objects
     * @param tagsParam
     * @return
     */
    private List<Tag> parseTags(String tagsParam){

        List<Tag> tags = new ArrayList<Tag>();

        if(tagsParam != null) {
            String[] tagTokens = parseMultiValueParam(tagsParam);
            if (tagTokens != null && tagTokens.length > 0) {
                for (String tagStr : tagTokens) {
                    Tag tag =  TagFactory.instance().createTag(tagStr);
                    tags.add(tag);
                }
            }
        }
        return tags;
    }
}
