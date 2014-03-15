package vidada.model.media;

import java.net.URI;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class ImageMediaItem extends MediaItem {
	/**
	 * ORM Constructor
	 */
	ImageMediaItem() {
		classinfo = "image";
	}

	/**
	 * Creates a copy of the given prototype
	 * @param prototype
	 */
	public ImageMediaItem(ImageMediaItem prototype){
		this();
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
		classinfo = "image";
	}

	@Override
	public ImageMediaItem clone() {
		return new ImageMediaItem(this);
	}

}
