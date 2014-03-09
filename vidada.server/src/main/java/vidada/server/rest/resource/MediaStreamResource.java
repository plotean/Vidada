package vidada.server.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.server.rest.VidadaRestServer;
import vidada.services.IMediaService;
import vidada.streaming.IStreamService;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.streaming.IResourceStreamServer;

@Path("/stream")
public class MediaStreamResource extends AbstractStreamResource {
	private final IMediaService mediaService = VidadaRestServer.VIDADA_SERVER.getMediaService();
	private final IStreamService streamService = ServiceProvider.Resolve(IStreamService.class);

	@GET
	//@Produces("video/mp4")
	@Path("{hash}")
	public Response getMedia(@PathParam("hash") String hash, @HeaderParam("Range") String range) {

		System.out.println("Server: Stream requested!");

		MediaItem media = mediaService.queryByHash(hash);

		if(media != null){
			MediaSource localSource = media.getSource();
			if(localSource != null){
				ResourceLocation resource = localSource.getResourceLocation();
				IResourceStreamServer streamServer = streamService.streamMedia(resource);
				System.out.println("stream @ " + streamServer.getUri());
				return Response.seeOther(streamServer.getUri()).build();
			}else{
				System.err.println("Server: Stream - Media has no source!");
			}
		}else {
			System.err.println("Server: Stream - No media found with hash " + hash);
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	/*
	@GET
	@Produces("video/mp4")
	@Path("{hash}")
	public Response getMedia(@PathParam("hash") String hash, @HeaderParam("Range") String range) {

		System.out.println("Server: Stream requested!");

		MediaItem media = mediaService.queryByHash(hash);

		if(media != null){
			MediaSource localSource = media.getSource();
			if(localSource != null){

				ResourceLocation resource = localSource.getResourceLocation();
				try {
					return buildStream(resource, range);
				} catch (Exception e) {
					e.printStackTrace();
					return Response.serverError().build();
				}
			}else{
				System.err.println("Server: Stream - Media has no source!");
			}
		}else {
			System.err.println("Server: Stream - No media found with hash " + hash);
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}*/

}
