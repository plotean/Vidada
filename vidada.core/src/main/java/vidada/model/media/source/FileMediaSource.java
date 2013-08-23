package vidada.model.media.source;

import java.beans.Transient;
import java.net.URI;

import vidada.model.ServiceProvider;
import vidada.model.libraries.MediaLibrary;
import vidada.model.media.MediaHashUtil;
import vidada.model.system.ISystemService;
import archimedesJ.io.locations.ResourceLocation;

import com.db4o.foundation.ArgumentNullException;

/**
 * Represents a local file media source
 * 
 * @author IsNull
 *
 */
public class FileMediaSource extends MediaSource {

	private MediaLibrary parentLibrary = null;
	private URI relativeFilePath = null;

	transient private boolean isFileAvaiableDirty = true;
	transient private boolean isFileAvaiable;

	public FileMediaSource(){}

	public FileMediaSource(MediaLibrary parentLibrary, URI relativeFilePath)
	{
		if(parentLibrary == null)
			throw new ArgumentNullException("parentLibrary");
		if(relativeFilePath == null)
			throw new ArgumentNullException("relativeFilePath");


		setParentLibrary(parentLibrary);
		setRelativeFilePath(relativeFilePath);
	}

	@Override
	public String getName() {
		ResourceLocation location = getAbsoluteFilePath();
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
			ResourceLocation absolutePath = getAbsoluteFilePath();
			isFileAvaiable = absolutePath != null && absolutePath.exists();
			isFileAvaiableDirty = false;
		}
		return isFileAvaiable;
	}


	/**
	 * Open this media By default, the media is opened with the standard tool
	 * registered in the current Operating System
	 */
	@Transient
	@Override
	public boolean open() {
		setIsAvailableDirty();
		ResourceLocation resource = getAbsoluteFilePath();
		ISystemService systemService = ServiceProvider.Resolve(ISystemService.class);
		return systemService.open(resource);
	}

	@Transient
	@Override
	public String retriveHash() {
		return MediaHashUtil.getDefaultMediaHashUtil()
				.retriveFileHash(getAbsoluteFilePath());
	}

	@Transient
	@Override
	public String getPath() {
		return getAbsoluteFilePath().toString();
	}


	// ----------------



	/**
	 * Gets the absolute file path to this media source
	 * @return
	 */
	@Transient
	public ResourceLocation getAbsoluteFilePath() {
		ResourceLocation absolutePath = null;
		URI relativePath = getRelativeFilePath();
		if(relativePath != null){
			absolutePath = getParentLibrary().getAbsolutePath(relativePath);
		}else {
			System.err.println("Source:getAbsoluteFilePath: relativePath is NULL!");
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
	 * Get the parent library of this media
	 * That is, the library holding this media
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
