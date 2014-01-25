package vidada.model.media.source;

import java.beans.Transient;
import java.net.URI;

import vidada.model.entities.BaseEntity;
import vidada.model.media.store.libraries.MediaLibrary;
import archimedesJ.io.locations.ResourceLocation;

import com.db4o.foundation.ArgumentNullException;

/**
 * Represents a media source. This can be a local file, a web resource, smb shared resource
 * or anything other which can deliver a stream
 * 
 * @author IsNull
 *
 */
public class MediaSourceLocal extends BaseEntity implements IMediaSource {

	private MediaLibrary parentLibrary = null;
	private URI relativeFilePath = null;

	transient private boolean isFileAvaiableDirty = true;
	transient private boolean isFileAvaiable;

	public MediaSourceLocal(){}

	public MediaSourceLocal(MediaLibrary parentLibrary, URI relativeFilePath)
	{
		if(parentLibrary == null)
			throw new ArgumentNullException("parentLibrary");
		if(relativeFilePath == null)
			throw new ArgumentNullException("relativeFilePath");


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
		isFileAvaiableDirty = true;
	}


	@Override
	public boolean isAvailable() {
		if (isFileAvaiableDirty) {
			ResourceLocation absolutePath = getResourceLocation();
			isFileAvaiable = absolutePath != null && absolutePath.exists();
			isFileAvaiableDirty = false;
		}
		return isFileAvaiable;
	}

	/*
	@Transient
	public String getPath() {
		return getResourceLocation().toString();
	}*/


	// ----------------



	/* (non-Javadoc)
	 * @see vidada.model.media.source.IMediaSource#getResourceLocation()
	 */
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
		return relativeFilePath;
	}

	public void setRelativeFilePath(URI relativefilePath) {
		this.relativeFilePath = relativefilePath;
		isFileAvaiableDirty = true;
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
		isFileAvaiableDirty = true;
	}


	@Override
	public String toString(){
		return "[" + getName() + "] avaiable: " + isAvailable();
	}
}


