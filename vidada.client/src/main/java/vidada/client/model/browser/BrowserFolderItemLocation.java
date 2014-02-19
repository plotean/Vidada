package vidada.client.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.model.media.MediaDirectory;
import vidada.model.media.MediaItem;
import vidada.services.IMediaService;
import archimedesJ.io.locations.DirectoryLocation;
import archimedesJ.io.locations.ResourceLocation;
import archimedesJ.io.locations.UniformLocation;

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
