package vidada.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;

public abstract class BrowserFolderItem implements IBrowserItem{

	private List<IBrowserItem> children = null;

	protected abstract List<IBrowserItem> loadChildren();

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public List<IBrowserItem> getChildren() {
		if(children == null) 
			children = loadChildren();
		return children;
	}

	@Override
	public MediaItem getData() { return null; }
}
