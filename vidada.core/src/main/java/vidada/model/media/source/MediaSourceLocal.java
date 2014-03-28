package vidada.model.media.source;

import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaLibrary;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a media source. This can be a local file, a web resource, smb shared resource
 * or anything other which can deliver a stream
 * 
 * @author IsNull
 *
 */
@Entity
@Access(AccessType.FIELD)
public class MediaSourceLocal extends MediaSource {

	transient private boolean isFileAvaiable;

	@ManyToOne
	private MediaLibrary parentLibrary = null;
	private String relativeFilePath = null;


	/** ORM Constructor */
	MediaSourceLocal(){ }

	public MediaSourceLocal(MediaLibrary parentLibrary, URI relativeFilePath)
	{
		if(parentLibrary == null)
			throw new IllegalArgumentException("parentLibrary");
		if(relativeFilePath == null)
			throw new IllegalArgumentException("relativeFilePath");


		setParentLibrary(parentLibrary);
		setRelativeFilePath(relativeFilePath);
	}

	public String getName() {
		ResourceLocation location = getResourceLocation();
		return location != null ? location.getName() : "AbsoluteFilePath == NULL";
	}

	/**
	 * Marks the isAvailable property as dirty and causes a refresh of the available state
	 * upon next property access
	 */
	public void setIsAvailableDirty(){
		//isFileAvaiableDirty = true;
	}


	@Override
	public boolean isAvailable() {
		ResourceLocation absolutePath = getResourceLocation();
		isFileAvaiable = absolutePath != null && absolutePath.exists();
		return isFileAvaiable;
	}

	// ----------------


	@Override
	@Transient
	public ResourceLocation getResourceLocation() {
		ResourceLocation absolutePath = null;
		URI relativePath = getRelativeFilePath();
		if(relativePath != null){
			MediaLibrary parentLib = getParentLibrary();
			if(parentLib != null){
				absolutePath = parentLib.getMediaDirectory().getAbsolutePath(relativePath);
			}else
				System.err.println("Source::getResourceLocation: parent library is null of " + relativeFilePath);
		}else {
			System.err.println("Source::getResourceLocation: relativePath is NULL!");
		}
		return absolutePath;
	}

	/**
	 * Gets the relative file path of this item. The relative path is based upon
	 * the media library path.
	 * 
	 * @return
	 */
	public URI getRelativeFilePath() {
		try {
			return new URI(relativeFilePath);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setRelativeFilePath(URI relativefilePath) {
		this.relativeFilePath = relativefilePath.toString();
	}

	/**
	 * Get the parent library which holds this media source
	 * @return
	 */
	public MediaLibrary getParentLibrary() {
		return parentLibrary;
	}

	protected void setParentLibrary(MediaLibrary parentLibrary) {
		this.parentLibrary = parentLibrary;
		//isFileAvaiableDirty = true;
	}


	@Override
	public String toString(){
		return "[" + getName() + "] avaiable: " + isAvailable();
	}
}


