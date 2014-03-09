package vidada.streaming;

import java.io.IOException;

import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.streaming.IResourceStreamServer;
import archimedesJ.streaming.http.StreamServerFactory;

public class StreamService implements IStreamService  {

	//private List<MediaStreamServer> mediaStreamServers = new ArrayList<MediaStreamServer>();


	/**
	 * Stream the given media in a wrapped http stream
	 */
	@Override
	public IResourceStreamServer streamMedia(ResourceLocation fileToStream) {

		IResourceStreamServer streamOverHttp = null;

		try {
			streamOverHttp = StreamServerFactory.createHttpStreamServer(fileToStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return streamOverHttp;
	}

}
