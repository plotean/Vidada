package vidada.model.media.images;

import java.net.URI;

import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.model.media.store.libraries.MediaLibrary;

public class ImageMediaItem extends MediaItem {



	/**
	 * Empty hibernate constructor
	 */
	public ImageMediaItem() {
	}

	/**
	 * Creates a copy of the given prototype
	 * @param prototype
	 */
	public ImageMediaItem(ImageMediaItem prototype){
		prototype(prototype);
	}

	/**
	 * Create a new Imagepart
	 * 
	 * @param parentLibrary
	 * @param relativeFilePath
	 * @param hash
	 */
	public ImageMediaItem(MediaLibrary parentLibrary, URI relativeFilePath, String hash) {
		super(parentLibrary, relativeFilePath);
		setFilehash(hash);
		setType(MediaType.IMAGE);
	}

}
