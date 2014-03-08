package vidada.client.composite;


/*
public class TagClientService extends ClientService implements ITagClientService{


	public TagClientService(VidadaClientManager manager) {
		super(manager);
	}

	@Override
	public synchronized Collection<Tag> getUsedTags(){
		Set<Tag> allTags = new HashSet<Tag>();
		Collection<IVidadaServer> servers = getClientManager().getAllClients();

		for (IVidadaServer server : servers) {
			Collection<Tag> serverTags = server.getTagService().getUsedTags();
			allTags.addAll(serverTags);
		}
		return allTags;
	}

}
 */