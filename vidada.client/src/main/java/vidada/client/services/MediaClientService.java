package vidada.client.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import vidada.IVidadaServer;
import vidada.client.VidadaClientManager;
import vidada.model.media.MediaComparator;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.OrderProperty;

public class MediaClientService extends ClientService implements IMediaClientService {


	public MediaClientService(VidadaClientManager manager){
		super(manager);
	}


	@Override
	public List<MediaItem> query(MediaQuery qry) {
		OrderProperty order = qry.getOrder();

		// We don't need results ordered since it will be 
		// scrambled by the Hash-set merging anyway:
		qry.setOrder(OrderProperty.NONE);
		Set<MediaItem> resultSet = new HashSet<MediaItem>();
		System.out.println("MediaClientService:: query " + getClientManager().getAllServer().size() + "  vidada servers!");
		for (IVidadaServer server : getClientManager().getAllServer()) {
			resultSet.addAll(server.getMediaService().query(qry));
		}

		// Now we have to sort the resultSet in memory
		// since we destroyed the order by using a hash map
		// and having multiple query result sets merged
		TreeSet<MediaItem> orderedMedias = new TreeSet<MediaItem>(MediaComparator.build(order, qry.isReverseOrder()));
		orderedMedias.addAll(resultSet);

		// restore order to original
		qry.setOrder(order);

		return new ArrayList<MediaItem>(orderedMedias);
	}


	@Override
	public void update(MediaItem media) {
		IVidadaServer server = getClientManager().getOriginServer(media);
		server.getMediaService().update(media);
	}



}
