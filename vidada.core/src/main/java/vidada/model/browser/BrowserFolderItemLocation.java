package vidada.model.browser;

import java.util.ArrayList;
import java.util.List;

import vidada.model.libraries.MediaDirectory;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
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
				MediaItem media = mediaService.findMediaData((ResourceLocation)location);
				item = new BrowserMediaItem(media);
			}
			if(item != null)
				children.add(item);
		}

		return children;
	}

}