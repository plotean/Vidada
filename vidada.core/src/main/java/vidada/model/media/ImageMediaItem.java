package vidada.model.media;

import java.net.URI;

import javax.persistence.Entity;

@Entity
public class ImageMediaItem extends MediaItem {
	/**
	 * ORM Constructor
	 */
	ImageMediaItem() {
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
