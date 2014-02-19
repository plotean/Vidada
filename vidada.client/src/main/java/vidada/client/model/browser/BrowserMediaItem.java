package vidada.client.model.browser;

import java.util.List;

import vidada.model.media.MediaItem;

public class BrowserMediaItem implements IBrowserItem {

	private final MediaItem data;

	public BrowserMediaItem(MediaItem data){
		this.data = data;
	}

	@Override
	public MediaItem getData() {
		return data;
	}

	/*
	public void setData(MediaItem data) {
		this.data = data;
	}*/

	@Override
	public final List<IBrowserItem> getChildren() {
		return null;
	}

	@Override
	public final boolean isFolder() {
		return false;
	}

	@Override
	public String getName() {
		return getData().getFilename();
	}

	@Override
	public String toString(){
		return getName();
	}
}
