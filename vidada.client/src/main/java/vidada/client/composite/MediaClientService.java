package vidada.client.composite;


/*
public class MediaClientService extends ClientService implements IMediaClientService {


	public MediaClientService(VidadaClientManager manager){
		super(manager);
	}


	@Override
	public ListPage<MediaItem> queryServer(IVidadaServer server, MediaQuery qry, int pageIndex, int maxPageSize) {
		System.out.println("MediaClientService:: query " + getClientManager().getAllClients().size() + "  vidada servers!");
		ListPage<MediaItem> page = server.getMediaService().query(qry, pageIndex, maxPageSize);
		return page;
	}*/

/*
	private ListPage<MediaItem> queryAllServers(MediaQuery qry, int pageIndex, int maxPageSize) {

		Set<MediaItem> resultSet = new HashSet<MediaItem>();
		System.out.println("MediaClientService:: query " + getClientManager().getAllServer().size() + "  vidada servers!");

		int totalRequestItems = 0;

		for (IVidadaServer server : getClientManager().getAllServer()) {
			ListPage<MediaItem> pagePart = server.getMediaService().query(qry, pageIndex, maxPageSize);

			// Merge the result
			totalRequestItems += pagePart.getTotalListSize();
			resultSet.addAll(pagePart.getPageItems());
		}

		// Now we have to sort the resultSet in memory
		// since we destroyed the order by using a hash map
		// and having multiple query result sets merged
		TreeSet<MediaItem> orderedMedias = new TreeSet<MediaItem>(
				MediaComparator.build(qry.getOrder(), qry.isReverseOrder()));

		// Adding the elements will sort them
		orderedMedias.addAll(resultSet);

		// Creates the page

		ListPage<MediaItem> page = new ListPage<MediaItem>(
				Lists.top(orderedMedias, maxPageSize),
				totalRequestItems,
				maxPageSize,
				pageIndex);

		return page;
	}*/

/*

	@Override
	public void update(MediaItem media) {
		IVidadaServer server = getClientManager().getOriginServer(media);
		server.getMediaService().update(media);
	}

}*/
