package vidada.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;
import archimedesJ.io.locations.ResourceLocation;

public class SimpleBrowserItem implements IBrowserItem {

	private ResourceLocation resource;

	public SimpleBrowserItem(ResourceLocation resource){
		this.resource = resource;
	}

	@Override
	public String getName() {
		return resource.getName();
	}

	@Override
	public List<IBrowserItem> getChildren() {
		return null;
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public MediaItem getData() {
		return null;
	}

}
