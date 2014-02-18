package vidada.client.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import vidada.IVidadaServer;
import vidada.client.VidadaClientManager;
import vidada.model.tags.Tag;

public class TagClientService extends ClientService implements ITagClientService{

	transient private final Map<String, Tag> tagNameCache = new HashMap<String, Tag>(5000);


	public TagClientService(VidadaClientManager manager) {
		super(manager);
	}


	@Override
	public synchronized Collection<Tag> getUsedTags(){
		Set<Tag> allTags = new HashSet<Tag>();
		Collection<IVidadaServer> servers = getClientManager().getAllServer();

		for (IVidadaServer server : servers) {
			Collection<Tag> serverTags = server.getTagService().getUsedTags();
			allTags.addAll(serverTags);
		}
		return allTags;
	}


	@Override
	public Set<Tag> createTags(String tagString) {
		Set<Tag> parsedTags = new HashSet<Tag>();
		String[] tags = tagString.split("[,|\\|]");

		String tagName;
		for (String t : tags) {
			tagName = t.trim();
			if(!tagName.isEmpty())
			{
				Tag newTag = getTag(tagName);
				if(newTag != null)
					parsedTags.add( newTag );
			}
		}

		return parsedTags;
	}


	@Override
	public Tag getTag(String tagName){
		tagName = tagName.toLowerCase();
		Tag tag = tagNameCache.get(tagName);
		if(tag == null){
			if(isValidTag(tagName)){
				tag = new Tag(tagName);
				tagNameCache.put(tagName, tag);
			}
		}
		return tag;
	}

	@Override
	public boolean isValidTag(String tagName){
		if(tagName == null || tagName.isEmpty())
			return false;
		return true;
	}


	@Override
	public void cacheTags(Collection<Tag> tags) {
		for (Tag tag : tags) {
			tagNameCache.put(tag.getName(), tag);
		}
	}
}
