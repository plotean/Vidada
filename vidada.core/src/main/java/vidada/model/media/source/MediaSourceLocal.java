package vidada.model.media.source;

import archimedes.core.io.locations.ResourceLocation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

    /***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/

    transient private static final Logger logger = LogManager.getLogger(MediaSourceLocal.class.getName());
	transient private boolean isFileAvailable;

	@ManyToOne
	private MediaLibrary parentLibrary = null;
	private String relativeFilePath = null;


	/** ORM Constructor */
	protected MediaSourceLocal(){ }

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
		isFileAvailable = absolutePath != null && absolutePath.exists();
		return isFileAvailable;
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
                logger.error("Parent library is null of " + relativeFilePath);
		}else {
            logger.error("getResourceLocation: relativePath is NULL!");
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
            logger.error(e);
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
	}


	@Override
	public String toString(){
		return "[" + getName() + "] available: " + isAvailable();
	}
}


