package vidada.client.model.browser;

import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.io.locations.ResourceLocation;
import archimedes.core.io.locations.UniformLocation;
import vidada.model.media.MediaDirectory;
import vidada.model.media.MediaItem;
import vidada.server.services.IMediaService;

import java.util.ArrayList;
import java.util.List;

public class BrowserFolderItemLocation extends BrowserFolderItem {
	private final DirectoryLocation directory;
	private final IMediaService mediaService;


	public BrowserFolderItemLocation(DirectoryLocation directory, IMediaService mediaService){
		this.directory = directory;
		this.mediaService = mediaService;
	}


	public DirectoryLocation getDirectoryLocation(){
		return directory;
	}

	@Override
	public String getName() {
		return directory.getName();
	}


	@Override
	protected List<IBrowserItem> loadChildren() {
		List<IBrowserItem> children = new ArrayList<IBrowserItem>();

		MediaDirectory mediaDirectory = new MediaDirectory(directory, false, false);
		List<UniformLocation> locations = mediaDirectory.getMediasAndFolders();

		IBrowserItem item = null;
		for (UniformLocation location : locations) {
			if(location instanceof DirectoryLocation)
			{
				item = new BrowserFolderItemLocation((DirectoryLocation)location, mediaService);
			}else if(location instanceof ResourceLocation){
				ResourceLocation resource = (ResourceLocation)location;
				MediaItem media = mediaService.findOrCreateMedia(resource, true);
				if(media != null)
					item = new BrowserMediaItem(media);
				else{
					System.err.println("could not find media for " + resource);
					item = new SimpleBrowserItem(resource);
				}
			}
			if(item != null)
				children.add(item);
		}

		return children;
	}

	@Override
	public String toString(){
		return "BrowserFolder:: " + getDirectoryLocation();
	}

}
